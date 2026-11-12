package operaciones;

import datos.*;
import notificaciones.ServicioNotificaciones;

import java.time.LocalDate;
import java.util.*;

/**
 * Reglas de negocio de reservas, pagos y transferencias:
 *  - Solo editoriales aprobadas reservan, y solo stands DISPONIBLES.
 *  - Con el 15% (configurable) pagado, la reserva pasa a OCUPADO.
 *  - Hay fecha límite para el pago total; si vence sin el anticipo, la reserva se cancela.
 *  - Descuento por pronto pago; pasada la fecha, se elimina.
 *  - BLOQUEADO = vendido desde antes: se manda a la editorial para que transfiera.
 */
public class GestorReservas {
    private final GestorStands stands;
    private final Configuracion config;
    private final ServicioNotificaciones avisos;
    private final RepositorioH2 repo; // puede ser null

    private final List<Reserva> reservas = new ArrayList<>();
    private final Map<Integer, Editorial> transferenciasPendientes = new HashMap<>();
    private int contador = 1;

    public GestorReservas(GestorStands stands, Configuracion config,
                          ServicioNotificaciones avisos, RepositorioH2 repo) {
        this.stands = stands;
        this.config = config;
        this.avisos = avisos;
        this.repo = repo;
    }

    public Reserva reservar(Editorial editorial, int numeroStand, LocalDate hoy) {
        Stand s = stands.buscarStand(numeroStand);
        if (s == null) throw new IllegalArgumentException("No existe el stand #" + numeroStand);
        if (!editorial.isAprobada())
            throw new IllegalStateException("La editorial " + editorial.getNombre() + " no está aprobada");
        if (s.getEstado() == EstadoStand.BLOQUEADO)
            throw new IllegalStateException("Stand #" + numeroStand + " bloqueado: use la transferencia");
        if (s.getEstado() != EstadoStand.DISPONIBLE)
            throw new IllegalStateException("Stand #" + numeroStand + " no está disponible");

        Reserva r = new Reserva(String.format("RES-%03d", contador++), editorial, s, hoy,
                hoy.plusDays(config.getDiasParaPagoTotal()), s.calcularCostoTotal());
        reservas.add(r);
        stands.asignar(s, EstadoStand.RESERVADO, editorial);
        guardar(r, editorial);
        avisos.anunciarReserva(r);
        return r;
    }

    public Optional<Reserva> buscarReserva(String identificador) {
        return reservas.stream().filter(r -> r.getIdentificador().equals(identificador)).findFirst();
    }

    public void registrarPago(String identificador, double monto, LocalDate fechaPago) {
        Reserva r = buscarReserva(identificador)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva " + identificador));
        if (!r.isActiva()) throw new IllegalStateException("La reserva está cancelada");
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido");

        r.agregarPago(monto);

        // Descuento por pronto pago: solo si se cubre todo antes de la fecha; pasada la fecha se elimina
        double original = r.getStand().calcularCostoTotal();
        double conDescuento = original * (1 - config.getPorcentajeDescuentoProntoPago());
        boolean enFecha = !fechaPago.isAfter(config.getFechaLimiteDescuento());
        if (enFecha && r.getMontoTotal() >= original - 0.005 && r.getMontoPagado() >= conDescuento - 0.005) {
            r.setMontoTotal(conDescuento);
            System.out.printf("[Descuento] Pronto pago aplicado a %s: total $%,.2f%n",
                    r.getIdentificador(), conDescuento);
        }

        // 15% pagado: pasa de reservado a ocupado
        Stand s = r.getStand();
        if (s.getEstado() == EstadoStand.RESERVADO
                && r.getPorcentajePagado() >= config.getPorcentajeAnticipo() - 1e-9) {
            stands.asignar(s, EstadoStand.OCUPADO, r.getEditorial());
            avisos.avisarStandOcupado(r);
        }
        guardar(r, r.getEditorial());
    }

    /** Cancela reservas vencidas sin anticipo y avisa de pagos por vencer. */
    public void verificarVencimientos(LocalDate hoy) {
        for (Reserva r : reservas) {
            if (!r.isActiva() || r.estaPagadaTotal()) continue;
            Stand s = r.getStand();
            if (hoy.isAfter(r.getFechaLimitePago()) && s.getEstado() == EstadoStand.RESERVADO) {
                r.setActiva(false);
                stands.asignar(s, EstadoStand.DISPONIBLE, null);
                avisos.avisarReservaCancelada(r);
                guardar(r, r.getEditorial());
            } else if (!hoy.isAfter(r.getFechaLimitePago())
                    && !hoy.plusDays(config.getDiasAvisoPrevio()).isBefore(r.getFechaLimitePago())) {
                avisos.avisarPagoPorVencer(r, hoy);
            }
        }
    }

    /** Stand bloqueado: se manda directo a la editorial para que transfiera (nunca pasa a disponible). */
    public void solicitarTransferencia(int numeroStand, Editorial editorial) {
        Stand s = stands.buscarStand(numeroStand);
        if (s == null || s.getEstado() != EstadoStand.BLOQUEADO)
            throw new IllegalStateException("El stand #" + numeroStand + " no está bloqueado");
        transferenciasPendientes.put(numeroStand, editorial);
        avisos.solicitarTransferencia(editorial.getCorreo(), numeroStand);
    }

    /** Al confirmarse la transferencia: desbloquea y ocupa el stand (BLOQUEADO -> OCUPADO). */
    public void confirmarTransferencia(int numeroStand) {
        Editorial e = transferenciasPendientes.remove(numeroStand);
        if (e == null) throw new IllegalStateException("No hay transferencia pendiente para #" + numeroStand);
        stands.asignar(stands.buscarStand(numeroStand), EstadoStand.OCUPADO, e);
    }

    public List<Reserva> getReservas() { return Collections.unmodifiableList(reservas); }

    private void guardar(Reserva r, Editorial e) {
        if (repo != null) {
            repo.guardarEditorial(e);
            repo.guardarReserva(r);
        }
    }
}

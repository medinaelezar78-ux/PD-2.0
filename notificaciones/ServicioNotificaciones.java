package notificaciones;

import datos.Reserva;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Arma los mensajes de cada evento y los manda por el Notificador. */
public class ServicioNotificaciones {
    private final Notificador notificador;

    public ServicioNotificaciones(Notificador notificador) {
        this.notificador = notificador;
    }

    /** Anuncio de que se reservó: monto de pago por vencer y fecha límite de pago. */
    public void anunciarReserva(Reserva r) {
        notificador.enviar(r.getEditorial().getCorreo(),
                "Reserva confirmada - Stand #" + r.getStand().getNumero(),
                String.format("Reserva %s registrada. Monto por pagar: $%,.2f. Fecha límite de pago: %s.",
                        r.getIdentificador(), r.getMontoPendiente(), r.getFechaLimitePago()));
    }

    public void avisarPagoPorVencer(Reserva r, LocalDate hoy) {
        long dias = ChronoUnit.DAYS.between(hoy, r.getFechaLimitePago());
        notificador.enviar(r.getEditorial().getCorreo(),
                "Pago por vencer - Stand #" + r.getStand().getNumero(),
                String.format("Le faltan %d día(s). Monto por vencer: $%,.2f. Fecha límite: %s.",
                        dias, r.getMontoPendiente(), r.getFechaLimitePago()));
    }

    public void avisarStandOcupado(Reserva r) {
        notificador.enviar(r.getEditorial().getCorreo(),
                "Stand #" + r.getStand().getNumero() + " ocupado",
                String.format("Se recibió el anticipo. Saldo pendiente: $%,.2f (límite %s).",
                        r.getMontoPendiente(), r.getFechaLimitePago()));
    }

    public void avisarReservaCancelada(Reserva r) {
        notificador.enviar(r.getEditorial().getCorreo(),
                "Reserva cancelada - Stand #" + r.getStand().getNumero(),
                "Venció la fecha límite sin cubrir el anticipo; el stand fue liberado.");
    }

    /** Stand bloqueado: se manda directo a la editorial para que haga la transferencia. */
    public void solicitarTransferencia(String correo, int numeroStand) {
        notificador.enviar(correo,
                "Transferencia de Stand #" + numeroStand,
                "El stand está bloqueado (vendido previamente). Realice la transferencia para "
                + "que se desbloquee y quede a su nombre.");
    }
}

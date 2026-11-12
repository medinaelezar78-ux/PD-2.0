package datos;

import java.time.LocalDate;

/** Reserva de un stand por una editorial: identificador, editorial, pagos y fecha límite. */
public class Reserva {
    private final String identificador;
    private final Editorial editorial;
    private final Stand stand;
    private final LocalDate fechaReserva;
    private final LocalDate fechaLimitePago;   // fecha límite para el pago total
    private double montoTotal;
    private double montoPagado;
    private boolean activa = true;

    public Reserva(String identificador, Editorial editorial, Stand stand,
                   LocalDate fechaReserva, LocalDate fechaLimitePago, double montoTotal) {
        this.identificador = identificador;
        this.editorial = editorial;
        this.stand = stand;
        this.fechaReserva = fechaReserva;
        this.fechaLimitePago = fechaLimitePago;
        this.montoTotal = montoTotal;
    }

    public double getMontoPendiente() { return Math.max(0, montoTotal - montoPagado); }
    public boolean estaPagadaTotal() { return montoPagado >= montoTotal - 0.005; }
    public double getPorcentajePagado() { return montoTotal == 0 ? 0 : montoPagado / montoTotal; }

    public String getIdentificador() { return identificador; }
    public Editorial getEditorial() { return editorial; }
    public Stand getStand() { return stand; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public LocalDate getFechaLimitePago() { return fechaLimitePago; }
    public double getMontoTotal() { return montoTotal; }
    public double getMontoPagado() { return montoPagado; }
    public boolean isActiva() { return activa; }

    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }
    public void agregarPago(double monto) { this.montoPagado += monto; }
    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() {
        return String.format("Reserva %s | %s | Stand #%d | Pagado $%,.2f de $%,.2f (%.0f%%) | Límite: %s%s",
                identificador, editorial.getNombre(), stand.getNumero(), montoPagado, montoTotal,
                getPorcentajePagado() * 100, fechaLimitePago, activa ? "" : " | CANCELADA");
    }
}

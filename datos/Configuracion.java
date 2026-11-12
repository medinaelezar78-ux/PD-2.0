package datos;

import java.time.LocalDate;

/**
 * Sistema de configuración (de las notas): parámetros editables para las
 * notificaciones y los datos de los stands.
 */
public class Configuracion {
    private double costoPorM2 = 1500.0;                 // costo m²
    private double porcentajeAnticipo = 0.15;           // 15% para pasar de reservado a ocupado
    private double porcentajeDescuentoProntoPago = 0.10; // descuento por pronto pago
    private LocalDate fechaLimiteDescuento;             // pasada esta fecha el descuento se elimina
    private int diasParaPagoTotal = 30;                 // plazo para cubrir todo el pago
    private int diasAvisoPrevio = 5;                    // días antes del vencimiento para avisar
    private String correoRemitente = "ferias@filey.mx";

    public Configuracion() {
        this.fechaLimiteDescuento = LocalDate.now().plusDays(15);
    }

    public double getCostoPorM2() { return costoPorM2; }
    public void setCostoPorM2(double v) { this.costoPorM2 = v; }
    public double getPorcentajeAnticipo() { return porcentajeAnticipo; }
    public void setPorcentajeAnticipo(double v) { this.porcentajeAnticipo = v; }
    public double getPorcentajeDescuentoProntoPago() { return porcentajeDescuentoProntoPago; }
    public void setPorcentajeDescuentoProntoPago(double v) { this.porcentajeDescuentoProntoPago = v; }
    public LocalDate getFechaLimiteDescuento() { return fechaLimiteDescuento; }
    public void setFechaLimiteDescuento(LocalDate v) { this.fechaLimiteDescuento = v; }
    public int getDiasParaPagoTotal() { return diasParaPagoTotal; }
    public void setDiasParaPagoTotal(int v) { this.diasParaPagoTotal = v; }
    public int getDiasAvisoPrevio() { return diasAvisoPrevio; }
    public void setDiasAvisoPrevio(int v) { this.diasAvisoPrevio = v; }
    public String getCorreoRemitente() { return correoRemitente; }
    public void setCorreoRemitente(String v) { this.correoRemitente = v; }
}

package datos;

/** Stand de la feria: dimensiones, m², ID, costo y estado (equivale al struct Stand de C). */
public class Stand {
    private final int numero;        // ID num
    private double ancho;
    private double largo;
    private double costoPorM2;
    private EstadoStand estado;
    private Editorial editorial;     // editorial asignada (null si está libre)

    public Stand(int numero, double ancho, double largo, double costoPorM2, EstadoStand estado) {
        if (ancho <= 0 || largo <= 0) throw new IllegalArgumentException("Dimensiones inválidas");
        this.numero = numero;
        this.ancho = ancho;
        this.largo = largo;
        this.costoPorM2 = costoPorM2;
        this.estado = estado;
    }

    /** El área no se guarda: se calcula al vuelo, igual que en C. */
    public double calcularArea() { return ancho * largo; }

    public double calcularCostoTotal() { return calcularArea() * costoPorM2; }

    public int getNumero() { return numero; }
    public double getAncho() { return ancho; }
    public double getLargo() { return largo; }
    public double getCostoPorM2() { return costoPorM2; }
    public EstadoStand getEstado() { return estado; }
    public Editorial getEditorial() { return editorial; }

    public void setDimensiones(double ancho, double largo) {
        if (ancho <= 0 || largo <= 0) throw new IllegalArgumentException("Dimensiones inválidas");
        this.ancho = ancho;
        this.largo = largo;
    }
    public void setCostoPorM2(double costoPorM2) { this.costoPorM2 = costoPorM2; }
    public void setEstado(EstadoStand estado) { this.estado = estado; }
    public void setEditorial(Editorial editorial) { this.editorial = editorial; }

    @Override
    public String toString() {
        return String.format("Stand #%d | Area: %.2f m2 (%.2fm x %.2fm) | Costo: $%,.2f | Estado: %s%s",
                numero, calcularArea(), ancho, largo, calcularCostoTotal(), estado.texto(),
                editorial != null ? " | Editorial: " + editorial.getNombre() : "");
    }
}

package datos;

/** Editorial que solicita espacio. Solo las aprobadas pueden reservar. */
public class Editorial {
    private final int id;
    private final String nombre;
    private final String correo;
    private boolean aprobada;

    public Editorial(int id, String nombre, String correo, boolean aprobada) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.aprobada = aprobada;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public boolean isAprobada() { return aprobada; }
    public void setAprobada(boolean aprobada) { this.aprobada = aprobada; }

    @Override
    public String toString() { return nombre + " (#" + id + ")"; }
}

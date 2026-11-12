package datos;

/** Los 4 estados del stand y su color en el mapa. */
public enum EstadoStand {
    DISPONIBLE("Verde", "\u001B[32m"),
    RESERVADO("Amarillo", "\u001B[33m"),
    OCUPADO("Rojo", "\u001B[31m"),
    BLOQUEADO("Gris", "\u001B[90m");

    private final String color;
    private final String ansi;

    EstadoStand(String color, String ansi) {
        this.color = color;
        this.ansi = ansi;
    }

    public String getColor() { return color; }
    public String getAnsi() { return ansi; }

    /** Equivalente a estadoAString() del código en C. */
    public String texto() {
        String n = name();
        return n.charAt(0) + n.substring(1).toLowerCase();
    }
}

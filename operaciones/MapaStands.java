package operaciones;

import datos.EstadoStand;
import datos.Stand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Mapa de lugares: verde=Disponible, amarillo=Reservado, rojo=Ocupado, gris=Bloqueado. */
public class MapaStands {
    private static final String RESET = "\u001B[0m";
    private static final int COLUMNAS = 4;

    public static void imprimir(List<Stand> stands) {
        List<Stand> porNumero = stands.stream()
                .sorted(Comparator.comparingInt(Stand::getNumero)).collect(Collectors.toList());

        System.out.println("=========== MAPA DE STANDS ===========");
        for (int i = 0; i < porNumero.size(); i++) {
            Stand s = porNumero.get(i);
            System.out.printf("%s[#%d %4.1fm2 %-10s]%s ", s.getEstado().getAnsi(), s.getNumero(),
                    s.calcularArea(), s.getEstado().texto(), RESET);
            if ((i + 1) % COLUMNAS == 0) System.out.println();
        }
        System.out.println();
        System.out.println("Leyenda: Verde=Disponible | Amarillo=Reservado | Rojo=Ocupado | Gris=Bloqueado");

        System.out.println("--- Lugares elegidos ---");
        boolean alguno = false;
        for (Stand s : porNumero) {
            if (s.getEditorial() != null && s.getEstado() != EstadoStand.DISPONIBLE) {
                System.out.printf("Stand #%d (%s) -> %s%n", s.getNumero(), s.getEstado().getColor(),
                        s.getEditorial().getNombre());
                alguno = true;
            }
        }
        if (!alguno) System.out.println("(ninguno)");
        System.out.println("======================================\n");
    }
}

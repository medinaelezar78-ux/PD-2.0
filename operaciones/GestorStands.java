package operaciones;

import datos.Editorial;
import datos.EstadoStand;
import datos.RepositorioH2;
import datos.Stand;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Lógica de negocio de los stands. Port de la lista enlazada de C: se mantiene
 * ordenada por área (menor a mayor). El recolector de basura de Java reemplaza
 * a free()/liberarLista().
 */
public class GestorStands {
    private final LinkedList<Stand> lista = new LinkedList<>();
    private final RepositorioH2 repo; // puede ser null

    public GestorStands(RepositorioH2 repo) {
        this.repo = repo;
    }

    /** crearStand() + insertarOrdenadoPorArea(): inserta en la posición según su área. */
    public void insertarOrdenadoPorArea(Stand nuevo) {
        if (nuevo == null) return;
        double area = nuevo.calcularArea();
        ListIterator<Stand> it = lista.listIterator();
        // Igual que en C: el nuevo va antes del primero con área >= a la suya
        while (it.hasNext()) {
            if (it.next().calcularArea() >= area) {
                it.previous();
                break;
            }
        }
        it.add(nuevo);
        if (repo != null) repo.guardarStand(nuevo);
    }

    public Stand buscarStand(int numero) {
        for (Stand s : lista) if (s.getNumero() == numero) return s;
        return null;
    }

    /** Si cambia el área, se saca y se reinserta para mantener el orden. */
    public boolean actualizarStand(int numero, double ancho, double largo, EstadoStand estado) {
        Stand s = buscarStand(numero);
        if (s == null) return false;
        double areaAnterior = s.calcularArea();
        s.setDimensiones(ancho, largo);
        s.setEstado(estado);
        if (areaAnterior != s.calcularArea()) {
            lista.remove(s);
            insertarOrdenadoPorArea(s);
        } else if (repo != null) {
            repo.guardarStand(s);
        }
        return true;
    }

    /** Cambia estado/editorial sin tocar el orden (el área no cambia). */
    public void asignar(Stand s, EstadoStand estado, Editorial editorial) {
        s.setEstado(estado);
        s.setEditorial(editorial);
        if (repo != null) repo.guardarStand(s);
    }

    public boolean borrarStand(int numero) {
        Stand s = buscarStand(numero);
        if (s == null) return false;
        lista.remove(s);
        if (repo != null) repo.borrarStand(numero);
        return true;
    }

    public void imprimirLista() {
        System.out.println("--- INVENTARIO FILEY (Ordenado por area) ---");
        if (lista.isEmpty()) System.out.println("La lista esta vacia.");
        else lista.forEach(System.out::println);
        System.out.println("--------------------------------------------\n");
    }

    public java.util.List<Stand> getStands() { return java.util.Collections.unmodifiableList(lista); }

    public void liberarLista() { lista.clear(); }
}

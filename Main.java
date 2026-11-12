import datos.*;
import notificaciones.NotificadorCorreo;
import notificaciones.ServicioNotificaciones;
import operaciones.GestorReservas;
import operaciones.GestorStands;
import operaciones.MapaStands;

import java.time.LocalDate;

/** Pruebas básicas (port de main.c) + los nuevos requisitos de las notas. */
public class Main {

    public static void main(String[] args) {
        Configuracion config = new Configuracion();
        LocalDate hoy = LocalDate.now();

        // H2 en memoria; si el jar no está en el classpath, se trabaja solo en memoria
        RepositorioH2 repo = null;
        try {
            repo = new RepositorioH2();
            System.out.println("[BD] H2 en memoria lista.");
        } catch (Exception | NoClassDefFoundError e) {
            System.out.println("[BD] H2 no disponible (" + e.getMessage() + "). Se continúa sin BD.");
        }

        GestorStands stands = new GestorStands(repo);
        ServicioNotificaciones avisos = new ServicioNotificaciones(new NotificadorCorreo(config));
        GestorReservas reservas = new GestorReservas(stands, config, avisos, repo);
        double c = config.getCostoPorM2();

        /* ---- Pruebas del código original ---- */
        System.out.println("\n[Prueba] Insertar stands ordenados por área:");
        stands.insertarOrdenadoPorArea(new Stand(101, 4.0, 3.0, c, EstadoStand.DISPONIBLE));
        stands.insertarOrdenadoPorArea(new Stand(102, 2.0, 2.5, c, EstadoStand.RESERVADO));
        stands.insertarOrdenadoPorArea(new Stand(103, 3.0, 5.0, c, EstadoStand.DISPONIBLE));
        stands.insertarOrdenadoPorArea(new Stand(104, 5.0, 3.0, c, EstadoStand.DISPONIBLE));
        stands.insertarOrdenadoPorArea(new Stand(105, 4.0, 2.5, c, EstadoStand.RESERVADO));
        stands.insertarOrdenadoPorArea(new Stand(106, 6.0, 5.0, c, EstadoStand.DISPONIBLE));
        // Nuevos: uno bloqueado (vendido desde antes) y uno libre para probar vencimientos
        stands.insertarOrdenadoPorArea(new Stand(107, 3.0, 3.0, c, EstadoStand.BLOQUEADO));
        stands.insertarOrdenadoPorArea(new Stand(108, 2.0, 2.0, c, EstadoStand.DISPONIBLE));
        stands.imprimirLista();

        System.out.println("[Prueba] Buscar stand #102:");
        Stand r = stands.buscarStand(102);
        System.out.println(r != null ? "Encontrado -> " + r : "No encontrado");

        System.out.println("\n[Prueba] Actualizar stand #103 (cambio de área y estado a Ocupado):");
        System.out.println(stands.actualizarStand(103, 1.0, 1.0, EstadoStand.OCUPADO)
                ? "Actualización exitosa" : "No se pudo actualizar");
        stands.imprimirLista();

        System.out.println("[Prueba] Borrar stand #106:");
        System.out.println(stands.borrarStand(106) ? "Eliminación exitosa" : "No se pudo eliminar");
        stands.imprimirLista();

        /* ---- Nuevos requisitos ---- */
        Editorial ed1 = new Editorial(1, "Editorial Sol", "sol@editorial.mx", true);
        Editorial ed2 = new Editorial(2, "Letras Norte", "letras@editorial.mx", true);
        Editorial ed3 = new Editorial(3, "Papel Nuevo", "papel@editorial.mx", false);
        Editorial ed4 = new Editorial(4, "Tinta Viva", "tinta@editorial.mx", true);

        System.out.println("[Prueba] Reserva + anticipo 15% -> Ocupado (stand #104):");
        Reserva r1 = reservas.reservar(ed1, 104, hoy);
        reservas.registrarPago(r1.getIdentificador(), r1.getMontoTotal() * 0.15, hoy);
        System.out.println(r1 + " | Estado: " + stands.buscarStand(104).getEstado().texto());

        System.out.println("\n[Prueba] Pago total a tiempo con descuento por pronto pago (stand #101):");
        Reserva r2 = reservas.reservar(ed2, 101, hoy);
        reservas.registrarPago(r2.getIdentificador(),
                r2.getMontoTotal() * (1 - config.getPorcentajeDescuentoProntoPago()), hoy);
        System.out.println(r2);

        System.out.println("\n[Prueba] Editorial no aprobada / stand bloqueado:");
        intentar(() -> reservas.reservar(ed3, 108, hoy));
        intentar(() -> reservas.reservar(ed4, 107, hoy));

        System.out.println("\n[Prueba] Transferencia de stand bloqueado (#107):");
        reservas.solicitarTransferencia(107, ed4);
        reservas.confirmarTransferencia(107);
        System.out.println(stands.buscarStand(107));

        System.out.println("\n[Prueba] Aviso de pagos por vencer y vencimiento sin anticipo (stand #108):");
        reservas.reservar(ed4, 108, hoy);
        reservas.verificarVencimientos(hoy.plusDays(config.getDiasParaPagoTotal() - 3));
        reservas.verificarVencimientos(hoy.plusDays(config.getDiasParaPagoTotal() + 1));
        System.out.println("Stand #108: " + stands.buscarStand(108).getEstado().texto());

        System.out.println("\nReservas:");
        reservas.getReservas().forEach(System.out::println);
        System.out.println();

        MapaStands.imprimir(stands.getStands());

        if (repo != null) {
            System.out.println("[BD] Stands guardados en H2: " + repo.contarStands());
            try { repo.close(); } catch (Exception ignored) { }
        }
        stands.liberarLista();
    }

    private static void intentar(Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException e) {
            System.out.println("Rechazado -> " + e.getMessage());
        }
    }
}

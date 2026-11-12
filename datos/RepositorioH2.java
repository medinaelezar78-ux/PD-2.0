package datos;

import java.sql.*;

/**
 * Base de datos H2 en memoria (jdbc:h2:mem). Se usa como respaldo de lo que maneja
 * la lógica de negocio. Requiere h2.jar en el classpath.
 */
public class RepositorioH2 implements AutoCloseable {
    private final Connection con;

    public RepositorioH2() throws SQLException {
        // DB_CLOSE_DELAY=-1 mantiene la BD viva mientras viva la JVM
        con = DriverManager.getConnection("jdbc:h2:mem:filey;DB_CLOSE_DELAY=-1", "sa", "");
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS editorial(id INT PRIMARY KEY, nombre VARCHAR(100), "
                    + "correo VARCHAR(100), aprobada BOOLEAN)");
            st.execute("CREATE TABLE IF NOT EXISTS stand(numero INT PRIMARY KEY, ancho DOUBLE, largo DOUBLE, "
                    + "costo_m2 DOUBLE, estado VARCHAR(20), editorial_id INT)");
            st.execute("CREATE TABLE IF NOT EXISTS reserva(identificador VARCHAR(30) PRIMARY KEY, "
                    + "editorial_id INT, stand_numero INT, fecha_reserva DATE, fecha_limite DATE, "
                    + "monto_total DOUBLE, monto_pagado DOUBLE, activa BOOLEAN)");
        }
    }

    public void guardarEditorial(Editorial e) {
        ejecutar("MERGE INTO editorial KEY(id) VALUES(?,?,?,?)", ps -> {
            ps.setInt(1, e.getId());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getCorreo());
            ps.setBoolean(4, e.isAprobada());
        });
    }

    public void guardarStand(Stand s) {
        ejecutar("MERGE INTO stand KEY(numero) VALUES(?,?,?,?,?,?)", ps -> {
            ps.setInt(1, s.getNumero());
            ps.setDouble(2, s.getAncho());
            ps.setDouble(3, s.getLargo());
            ps.setDouble(4, s.getCostoPorM2());
            ps.setString(5, s.getEstado().name());
            if (s.getEditorial() == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, s.getEditorial().getId());
        });
    }

    public void borrarStand(int numero) {
        ejecutar("DELETE FROM stand WHERE numero=?", ps -> ps.setInt(1, numero));
    }

    public void guardarReserva(Reserva r) {
        ejecutar("MERGE INTO reserva KEY(identificador) VALUES(?,?,?,?,?,?,?,?)", ps -> {
            ps.setString(1, r.getIdentificador());
            ps.setInt(2, r.getEditorial().getId());
            ps.setInt(3, r.getStand().getNumero());
            ps.setDate(4, Date.valueOf(r.getFechaReserva()));
            ps.setDate(5, Date.valueOf(r.getFechaLimitePago()));
            ps.setDouble(6, r.getMontoTotal());
            ps.setDouble(7, r.getMontoPagado());
            ps.setBoolean(8, r.isActiva());
        });
    }

    public int contarStands() {
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM stand")) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @FunctionalInterface
    private interface Params { void set(PreparedStatement ps) throws SQLException; }

    private void ejecutar(String sql, Params p) {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            p.set(ps);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Error en BD: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void close() throws SQLException { con.close(); }
}

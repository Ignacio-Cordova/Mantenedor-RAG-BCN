package cl.bcn.util;
import cl.bcn.model.BaseDeDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import cl.bcn.model.Conexion;
/**
 * Extrae texto de fuentes PostgreSQL mediante JDBC.
 * Ejecuta consultas o tablas configuradas y aplica un límite opcional para previsualizaciones.
 */

public class ExtractorJDBC {
    public static List<String> extraerTextos(BaseDeDatos bd) throws Exception {
        return extraerTextos(bd, null);
    }
    public static List<String> extraerTextos(BaseDeDatos bd, Integer limite) throws Exception {
        
        if (bd == null) {
            throw new Exception("No se recibió la configuración de la fuente JDBC.");
        }

        List<String> textosExtraidos = new ArrayList<>();
        Conexion connInfo = bd.getConexion();
        
        if (connInfo == null) {
            throw new Exception("Error: La fuente no tiene una conexión configurada.");
        }

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            if (connInfo.getMotor() == null || !"POSTGRESQL".equalsIgnoreCase(connInfo.getMotor())) {
                throw new Exception("Motor JDBC no soportado: " + connInfo.getMotor()
                        + ". Actualmente solo se admite PostgreSQL.");
            }

            if ("POSTGRESQL".equalsIgnoreCase(connInfo.getMotor())) {
                String jdbcUrl = "jdbc:postgresql://" + connInfo.getHost() + ":" + connInfo.getPuerto() + "/" + connInfo.getNombre();
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(jdbcUrl, connInfo.getUsuario(), connInfo.getPassword());
                String sql = bd.getQuerySql(); 
                if (sql == null || sql.trim().isEmpty()) {
                    sql = "SELECT * FROM " + bd.getTablaObjetivo();
                }
                
                ps = connection.prepareStatement(sql);
                
                // El límite se aplica al driver para evitar cargar más filas de las requeridas por una previsualización.
                if (limite != null && limite > 0) {
                    ps.setMaxRows(limite);
                }

                rs = ps.executeQuery();
                String columnaContenido = bd.getColumnaContenido();

                while (rs.next()) {
                    String texto = rs.getString(columnaContenido);
                    if (texto != null && !texto.trim().isEmpty()) {
                        textosExtraidos.add(texto);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al extraer texto JDBC: " + e.getMessage());
            throw new Exception("No se pudo extraer texto de la fuente JDBC: " + e.getMessage(), e);
            
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (ps != null) try { ps.close(); } catch (Exception e) {}
            if (connection != null) try { connection.close(); } catch (Exception e) {}
        }

        return textosExtraidos;
    }
}
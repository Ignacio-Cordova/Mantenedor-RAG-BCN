package cl.bcn.commands;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Prueba credenciales PostgreSQL por JDBC y devuelve las tablas visibles para el usuario configurado.
 */
public class TestConnectionCommand extends AbstractCommand<JSONObject, PersistenceFactory> {

    @Override
    protected void validateParams() throws Exception {
    	validateRule(validator.validateNotEmpty("host"));
    	validateRule(validator.validateNotEmpty("puerto"));
    	validateRule(validator.validateNotEmpty("usuario"));
    	validateRule(validator.validateNotEmpty("password"));
    	validateRule(validator.validateNotEmpty("motor"));
    	validateRule(validator.validateNotEmpty("nombre")); 
    }

    @Override
    protected JSONObject execute() throws Exception { 
        String host = getParam("host");
        Integer puerto = getIntParam("puerto");
        String usuario = getParam("usuario");
        String password = getParam("password");
        String motor = getParam("motor");
        String dbName = getParam("nombre"); 
        Connection connExterna = null;
        ResultSet rs = null;
        JSONArray listaTablas = new JSONArray();

        try {
            String urlJdbc = "";
            if ("POSTGRESQL".equalsIgnoreCase(motor)) {
                Class.forName("org.postgresql.Driver");
                urlJdbc = "jdbc:postgresql://" + host + ":" + puerto + "/" + dbName;
            } else {
                throw new Exception("Motor no soportado: " + motor
                        + ". Actualmente solo se admite PostgreSQL.");
            }
            connExterna = DriverManager.getConnection(urlJdbc, usuario, password);
            DatabaseMetaData metaData = connExterna.getMetaData();
            rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String nombreTabla = rs.getString(3);
                listaTablas.put(nombreTabla);
            }
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); 
            throw new Exception("Error interno: No tengo el Driver (.jar) para " + motor);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("No se pudo conectar: " + e.getMessage());
        } finally {
            if (rs != null) rs.close();
            if (connExterna != null) connExterna.close();
        }
        JSONObject respuesta = new JSONObject();
        respuesta.put("tablas", listaTablas);

        return respuesta;
    }
}
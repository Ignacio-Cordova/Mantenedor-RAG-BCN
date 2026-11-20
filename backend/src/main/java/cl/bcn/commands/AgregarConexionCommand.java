package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import java.util.Date;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Conexion;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ConexionDAO;
/**
 * Registra una conexión PostgreSQL con nombre, host, puerto, usuario, contraseña y motor en PostgreSQL.
 */
public class AgregarConexionCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
		String nombre = getParam("nombre");
		Integer puerto = getIntParam("puerto");
		String password = getParam("password");
		String usuario = getParam("usuario");
		String host = getParam("host");
		String motor = getParam("motor");
		Conexion conexion = new Conexion();
		conexion.setNombre(nombre);
		conexion.setPuerto(puerto);
		conexion.setPassword(password);
		conexion.setUsuario(usuario);
		conexion.setHost(host);
		conexion.setMotor(motor);
		conexion.setCrdate(new Date());
		ConexionDAO dao = getDAOFactory().getConexionDAO();
		Integer nuevoId = dao.insert(conexion);
		conexion.setId(nuevoId);
		dao.update(conexion);
		JSONObject response = new JSONObject();	
		response.put("success", true);
		response.put("id_conexion", nuevoId);
		
		return response;
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("nombre"));
		validateRule(validator.validateNotEmpty("puerto"));
		validateRule(validator.validateNotEmpty("password"));
		validateRule(validator.validateNotEmpty("usuario"));
		validateRule(validator.validateNotEmpty("host"));
		validateRule(validator.validateNotEmpty("motor"));
		validateRule(validator.validateInt("puerto"));
	}

}

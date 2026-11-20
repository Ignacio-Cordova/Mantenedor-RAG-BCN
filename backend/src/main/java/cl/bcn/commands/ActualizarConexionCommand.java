package cl.bcn.commands;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Conexion;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ConexionDAO;
/**
 * Actualiza una conexión PostgreSQL por id y reemplaza host, puerto, usuario, contraseña y motor persistidos.
 */
public class ActualizarConexionCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		
		Integer id = getIntParam("id");
		ConexionDAO dao = getDAOFactory().getConexionDAO();
		Conexion conexion = dao.findById(id);
		
		if (conexion == null) {
			throw new Exception("Conexion no encontrada con id: " + id);
		}
		
		conexion.setId(id);
		conexion.setNombre(getParam("nombre"));
		conexion.setMotor(getParam("motor"));
		conexion.setHost(getParam("host"));
		conexion.setPuerto(getIntParam("puerto"));
		conexion.setUsuario(getParam("usuario"));
		conexion.setPassword(getParam("password"));
		
		dao.update(conexion);
		
		return new JSONObject().put("status", "success");
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
	}

}

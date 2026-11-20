package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.Dataset;
import org.json.JSONObject;

import java.util.List;

import org.json.JSONArray;


import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Conexion;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ConexionDAO;
/**
 * Lista conexiones PostgreSQL con limit y offset y devuelve data y conteo de la página.
 */
public class ObtenerConexionesPaginadasCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
		ConexionDAO dao = getDAOFactory().getConexionDAO();
		List<Conexion> conexiones = dao.findAll(getIntParam("limit"), getIntParam("offset"));
		int totalRegistros = conexiones.size();
		JSONObject response = new JSONObject();
		response.put("data", new JSONArray(conexiones));
		response.put("total", totalRegistros);
		
		return response;
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("limit"));
		validateRule(validator.validateNotEmpty("offset"));		
		validateRule(validator.validateInt("limit"));
		validateRule(validator.validateInt("offset"));
	}

}
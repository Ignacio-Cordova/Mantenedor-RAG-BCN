package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.persistence.dao.ApiDAO;
import cl.bcn.model.Dataset;
import org.json.JSONObject;

import java.util.List;

import org.json.JSONArray;


import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Api;
import cl.bcn.model.Conexion;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ConexionDAO;
/**
 * Lista proveedores de API con limit y offset y devuelve data más el total de la página actual.
 */
public class ObtenerApisPaginadasCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
		ApiDAO dao = getDAOFactory().getApiDAO();
		List<Api> conexiones = dao.findAll(getIntParam("limit"), getIntParam("offset"));
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
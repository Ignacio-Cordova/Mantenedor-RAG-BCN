package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.BaseDeDatos;
import cl.bcn.persistence.dao.BaseDeDatosDAO;
import org.json.JSONObject;

import java.util.List;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Lista fuentes JDBC PostgreSQL de un dataset con paginación y devuelve el total asociado.
 */
public class ObtenerBDPorDatasetPaginadosCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
		BaseDeDatosDAO dao = getDAOFactory().getBaseDeDatosDAO();
		List<BaseDeDatos> basesDeDatos = dao.findAll(getIntParam("id"), getIntParam("limit"), getIntParam("offset"));
		int totalRegistros = dao.countByDataset(getIntParam("id"));
		JSONArray jsonArray = new JSONArray();
		for (BaseDeDatos bdd : basesDeDatos) {
		    jsonArray.put(bdd.toJSON()); 
		}
		JSONObject response = new JSONObject();
		response.put("data", jsonArray);
		response.put("total", totalRegistros);

		return response;
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateNotEmpty("limit"));
		validateRule(validator.validateNotEmpty("offset"));		
		validateRule(validator.validateInt("id"));
		validateRule(validator.validateInt("limit"));
		validateRule(validator.validateInt("offset"));		
	}

}
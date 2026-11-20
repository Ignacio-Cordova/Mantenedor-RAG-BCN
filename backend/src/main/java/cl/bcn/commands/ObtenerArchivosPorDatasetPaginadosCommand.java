package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.persistence.dao.ArchivoDAO;
import cl.bcn.model.Archivo;
import org.json.JSONObject;

import java.util.List;

import org.json.JSONArray;


import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Lista archivos de un dataset con paginación y cuenta el total de fuentes ARCHIVO.
 */
public class ObtenerArchivosPorDatasetPaginadosCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
		ArchivoDAO dao = getDAOFactory().getArchivoDAO();
		List<Archivo> archivo = dao.findAll(getIntParam("id"), getIntParam("limit"), getIntParam("offset"));
		int totalRegistros = dao.countByDataset(getIntParam("id"));
		JSONObject response = new JSONObject();
		response.put("data", new JSONArray(archivo));
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
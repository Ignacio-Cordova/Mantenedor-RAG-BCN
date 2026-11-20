package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.ModeloLlm;
import org.json.JSONObject;

import java.util.List;

import org.json.JSONArray;


import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloLlmDAO;
/**
 * Lista modelos LLM con limit y offset y devuelve data y conteo de la página.
 */
public class ObtenerModelosLlmPaginadosCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
	    ModeloLlmDAO dao = getDAOFactory().getModeloLlmDAO();
	    List<ModeloLlm> conexiones = dao.findAll(getIntParam("limit"), getIntParam("offset"));
	    int totalRegistros = conexiones.size(); 
	    JSONArray jsonArray = new JSONArray();
	    for (ModeloLlm modelo : conexiones) {
	        jsonArray.put(modelo.toJSON());
	    }
	    JSONObject response = new JSONObject();
	    response.put("data", jsonArray);
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
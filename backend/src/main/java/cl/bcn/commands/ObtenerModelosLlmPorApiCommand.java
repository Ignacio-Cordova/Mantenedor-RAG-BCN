package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.ModeloLlm;


import org.json.JSONArray;

import java.util.List;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloLlmDAO;
/**
 * Lista los modelos LLM asociados a la API recibida en el parámetro id.
 */
public class ObtenerModelosLlmPorApiCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}

	@Override
	protected JSONArray execute() throws Exception {
	    ModeloLlmDAO dao = getDAOFactory().getModeloLlmDAO();
	    List<ModeloLlm> lista = dao.findByIdApi(getIntParam("id"));
	    JSONArray jsonArray = new JSONArray();
	    
	    for (ModeloLlm modelo : lista) {
	        jsonArray.put(modelo.toJSON()); 
	    }
	    
	    return jsonArray;
	}
}

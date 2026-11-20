package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.ModeloLlm;

import java.util.List;

import org.json.JSONArray;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloLlmDAO;
/**
 * Lista los modelos LLM registrados mediante ModeloLlm.toJSON().
 */
public class ObtenerModelosLlmCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
	}

	@Override
	protected JSONArray execute() throws Exception {
	    ModeloLlmDAO dao = getDAOFactory().getModeloLlmDAO();
	    List<ModeloLlm> lista = dao.findAll();
	    JSONArray jsonArray = new JSONArray();
	    
	    for (ModeloLlm modelo : lista) {
	        jsonArray.put(modelo.toJSON()); 
	    }
	    
	    return jsonArray;
	}
}
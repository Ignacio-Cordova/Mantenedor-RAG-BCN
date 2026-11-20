package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;

import java.util.List;

import org.json.JSONArray;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloEmbeddingDAO;
/**
 * Lista los modelos Embedding registrados mediante ModeloEmbedding.toJSON().
 */
public class ObtenerModelosEmbeddingCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
	}

	@Override
	protected JSONArray execute() throws Exception {
	    ModeloEmbeddingDAO dao = getDAOFactory().getModeloEmbeddingDAO();
	    List<ModeloEmbedding> lista = dao.findAll();
	    JSONArray jsonArray = new JSONArray();
	    for (ModeloEmbedding modelo : lista) {
	        jsonArray.put(modelo.toJSON()); 
	    }
	    
	    return jsonArray;
	}
}
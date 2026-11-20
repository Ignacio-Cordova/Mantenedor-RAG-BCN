package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.RetrievalMethod;

import org.json.JSONArray;
import java.util.List;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.RetrievalMethodDAO;
/**
 * Lista los métodos de recuperación disponibles mediante RetrievalMethod.toJSON().
 */
public class ObtenerRetrievalMethodsCommand  extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {


	@Override
	protected JSONArray execute() throws Exception {
		RetrievalMethodDAO dao = getDAOFactory().getRetrievalMethodDAO();
		List<RetrievalMethod> retrievalM = dao.findAll();
		JSONArray jsonArray = new JSONArray();
	    for (RetrievalMethod rm : retrievalM) {
	        jsonArray.put(rm.toJSON()); 
	    }
	    
	    return jsonArray;
	}	
	
	@Override
	protected void validateParams() throws Exception {
	}

}
package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.IndexMethod;

import java.util.List;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.IndexMethodDAO;
/**
 * Lista los métodos de fragmentación disponibles mediante IndexMethod.toJSON().
 */
public class ObtenerIndexMethodsCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService{

	@Override
	protected JSONArray execute() throws Exception {
		IndexMethodDAO IndexDAO = getDAOFactory().getIndexMethodDAO();
		List<IndexMethod> indexM = IndexDAO.findAll();
		JSONArray jsonArray = new JSONArray();
	    
	    for (IndexMethod ds : indexM) {
	        jsonArray.put(ds.toJSON()); 
	    }
	    
	    return jsonArray;
	}

	@Override
	protected void validateParams() throws Exception {
	}
	
}
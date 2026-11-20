package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.Dataset;

import java.util.List;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DatasetDAO;
/**
 * Lista todos los datasets usando Dataset.toJSON().
 */
public class ObtenerDatasetsCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
	}

	@Override
	protected JSONArray execute() throws Exception {
		DatasetDAO dao = getDAOFactory().getDatasetDAO();
	    List<Dataset> listaDatasets = dao.findAll();
	    JSONArray jsonArray = new JSONArray();
	    
	    for (Dataset ds : listaDatasets) {
	        jsonArray.put(ds.toJSON()); 
	    }
	    
	    return jsonArray;
	}
}

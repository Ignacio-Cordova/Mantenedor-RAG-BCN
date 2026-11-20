package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;

import org.json.JSONArray;

import java.util.List;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.FuenteDAO;
/**
 * Lista las fuentes asociadas a un dataset y devuelve sus estados de indexación.
 */
public class ObtenerFuentesPorDatasetCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONArray execute() throws Exception {
		FuenteDAO dao = getDAOFactory().getFuenteDAO();
		List<Fuente> fuentes = dao.findAllByDataset(getIntParam("id"));
		JSONArray jsonArray = new JSONArray();
	    
	    for (Fuente f : fuentes) {
	        jsonArray.put(f.toJSON()); 
	    }
	    
	    return jsonArray;
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}


}
package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.IndexMethod;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.IndexMethodDAO;
/**
 * Obtiene por id un método de fragmentación y su descripción.
 */
public class ObtenerIndexMethodPorIdCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
		IndexMethodDAO dao = getDAOFactory().getIndexMethodDAO();
		IndexMethod index_method = dao.findById(getIntParam("id"));
		if (index_method == null) {
		    return new JSONObject().put("error", "Index Method no encontrado");
		}
		return index_method.toJSON();
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));		
		validateRule(validator.validateInt("id"));		
	}

}
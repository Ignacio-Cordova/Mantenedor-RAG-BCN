package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.Dataset;
import org.json.JSONObject;


import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DatasetDAO;
/**
 * Obtiene un dataset por id y devuelve sus relaciones en Dataset.toJSON().
 */
public class ObtenerDatasetPorIdCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}

	@Override
	protected JSONObject execute() throws Exception {
		DatasetDAO dao = getDAOFactory().getDatasetDAO();
		Dataset dataset = dao.findById(getIntParam("id"));

		if (dataset == null) {
		    JSONObject errorResponse = new JSONObject();
		    errorResponse.put("error", "No se encontró el Dataset con el ID especificado.");
		    return errorResponse;
		}

		return dataset.toJSON();
	}
}
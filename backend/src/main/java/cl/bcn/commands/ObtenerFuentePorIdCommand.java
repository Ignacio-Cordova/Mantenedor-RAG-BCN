package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Obtiene una fuente por id y devuelve sus estado, dataset y metadatos mediante Fuente.toJSON().
 */
public class ObtenerFuentePorIdCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
		Integer id = getIntParam("id");
		Fuente fuente = getDAOFactory().getFuenteDAO().findById(id);
		if (fuente == null) {
		    return new JSONObject().put("error", "Fuente no encontrada");
		}
		return fuente.toJSON();
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}
}

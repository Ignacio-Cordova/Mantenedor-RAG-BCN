package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.ModeloLlm;
import org.json.JSONObject;


import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloLlmDAO;
/**
 * Obtiene por id un modelo LLM y devuelve endpoint, plantilla, ruta de respuesta y API asociada.
 */
public class ObtenerModeloLlmPorIdCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}

	@Override
	protected JSONObject execute() throws Exception {
		ModeloLlmDAO dao = getDAOFactory().getModeloLlmDAO();
		ModeloLlm modelo_llm = dao.findById(getIntParam("id"));
		if (modelo_llm == null) {
		    return new JSONObject().put("error", "Modelo LLM no encontrado");
		}
		return modelo_llm.toJSON();
	}
}
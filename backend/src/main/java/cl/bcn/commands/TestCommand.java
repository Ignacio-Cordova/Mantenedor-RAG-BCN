package cl.bcn.commands;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.commands.params.FormDataParams;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Devuelve una respuesta mínima con los parámetros test y number para verificar Cacatua y validación.
 */
public class TestCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService, FormDataParams {

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateInt("number"));
		validateRule(validator.validateMinLength("test", 4));
	}

	@Override
	protected JSONObject execute() throws Exception {
		JSONObject o = getOkDefaultResult();
			
		o.put("number", getIntParam("number"));
		o.put("test", getParam("test"));
		return o;
	}
}

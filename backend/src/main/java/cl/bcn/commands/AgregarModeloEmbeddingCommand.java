package cl.bcn.commands;
import java.util.Date;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Registra un modelo Embedding con nombre técnico, dimensiones, máximo de tokens y API asociada.
 */
public class AgregarModeloEmbeddingCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		ModeloEmbedding modelo = new ModeloEmbedding();
		modelo.setNombreTecnico(getParam("nombre_tecnico"));
		modelo.setDimensiones(getIntParam("dimensiones"));
		modelo.setMaxTokensInput(getIntParam("max_tokens_input"));
		modelo.setApi(getDAOFactory().getApiDAO().findById(getIntParam("id_api")));
		modelo.setCrdate(new java.util.Date());

		Integer id = getDAOFactory().getModeloEmbeddingDAO().insert(modelo);
		return new JSONObject()
				.put("status", "success")
				.put("id", id);
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("nombre_tecnico"));
		validateRule(validator.validateInt("dimensiones"));
		validateRule(validator.validateInt("max_tokens_input"));
		validateRule(validator.validateInt("id_api"));
	}
}

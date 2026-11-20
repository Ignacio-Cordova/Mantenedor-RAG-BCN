package cl.bcn.commands;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Actualiza por id el nombre técnico, dimensiones, máximo de tokens y API asociada de un modelo Embedding.
 */
public class ActualizarModeloEmbeddingCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		Integer id = getIntParam("id");
		ModeloEmbedding modelo = getDAOFactory().getModeloEmbeddingDAO().findById(id);
		if (modelo == null) {
			throw new Exception("Modelo de embedding no encontrado con id: " + id);
		}

		modelo.setNombreTecnico(getParam("nombre_tecnico"));
		modelo.setDimensiones(getIntParam("dimensiones"));
		modelo.setMaxTokensInput(getIntParam("max_tokens_input"));
		modelo.setApi(getDAOFactory().getApiDAO().findById(getIntParam("id_api")));

		int filas = getDAOFactory().getModeloEmbeddingDAO().update(modelo);
		return new JSONObject()
				.put("status", "success")
				.put("filasAfectadas", filas);
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateInt("id"));
		validateRule(validator.validateNotEmpty("nombre_tecnico"));
		validateRule(validator.validateInt("dimensiones"));
		validateRule(validator.validateInt("max_tokens_input"));
		validateRule(validator.validateInt("id_api"));
	}
}

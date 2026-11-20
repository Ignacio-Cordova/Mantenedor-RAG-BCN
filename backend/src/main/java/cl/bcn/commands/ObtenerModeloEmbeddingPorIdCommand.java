package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import org.json.JSONObject;


import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloEmbeddingDAO;
/**
 * Obtiene por id un modelo Embedding y devuelve dimensiones, tokens y API asociada.
 */
public class ObtenerModeloEmbeddingPorIdCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}

	@Override
	protected JSONObject execute() throws Exception {
		ModeloEmbeddingDAO dao = getDAOFactory().getModeloEmbeddingDAO();
		ModeloEmbedding modelo_embedding = dao.findById(getIntParam("id"));
		return modelo_embedding.toJSON();
	}
}
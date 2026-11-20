package cl.bcn.commands;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Elimina un modelo Embedding solo si no está asociado a ningún dataset activo.
 */
public class EliminarModeloEmbeddingCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		Integer id = getIntParam("id");
		try {
			int filas = getDAOFactory().getModeloEmbeddingDAO().delete(id);
			return new JSONObject()
					.put("status", "success")
					.put("message", "Modelo de embedding eliminado correctamente.")
					.put("filasAfectadas", filas);
		} catch (Exception e) {
			return new JSONObject().put("error", e.getMessage());
		}
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateInt("id"));
	}
}

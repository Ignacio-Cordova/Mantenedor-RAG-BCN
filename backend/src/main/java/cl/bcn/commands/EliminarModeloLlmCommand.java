package cl.bcn.commands;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloLlmDAO;
/**
 * Elimina un modelo LLM solo si no está asociado a ningún dataset activo.
 */
public class EliminarModeloLlmCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		ModeloLlmDAO dao = getDAOFactory().getModeloLlmDAO();
		Integer idModelo = getIntParam("id");
		
		try {
			Integer filasAfectadas = dao.delete(idModelo);
			return new JSONObject()
					.put("status", "success")
					.put("message", "Modelo LLM eliminado correctamente.")
					.put("filasAfectadas", filasAfectadas);
					
		} catch (Exception e) {
			System.err.println("Aviso BD: No se pudo eliminar el modelo LLM ID " + idModelo + ". Detalle: " + e.getMessage());
			
			return new JSONObject()
					.put("error", e.getMessage());
		}
		
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));	
		validateRule(validator.validateInt("id"));
	}

}

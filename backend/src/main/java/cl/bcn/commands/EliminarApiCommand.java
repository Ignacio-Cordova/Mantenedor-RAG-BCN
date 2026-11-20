package cl.bcn.commands;
import cl.bcn.persistence.dao.ApiDAO;
import cl.bcn.model.Api;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Elimina una API solo si no está referenciada por datasets o modelos LLM/Embedding.
 */
public class EliminarApiCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		
		ApiDAO dao = getDAOFactory().getApiDAO();
		Integer idApi = getIntParam("id");
		
		try {
			Integer filasAfectadas = dao.delete(idApi);
			return new JSONObject()
					.put("status", "success")
					.put("message", "Proveedor de API eliminado correctamente.")
					.put("filasAfectadas", filasAfectadas);
					
		} catch (Exception e) {
			System.err.println("Aviso BD: No se pudo eliminar el proveedor de API ID " + idApi + ". Detalle: " + e.getMessage());
			
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

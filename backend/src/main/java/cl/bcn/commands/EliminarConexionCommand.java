package cl.bcn.commands;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ConexionDAO;
/**
 * Elimina una conexión PostgreSQL solo si no posee fuentes de base de datos asociadas.
 */
public class EliminarConexionCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {
	
	@Override
	protected JSONObject execute() throws Exception {
		ConexionDAO conexionDAO = getDAOFactory().getConexionDAO();
		Integer idConexion = getIntParam("id");
				
		try {
			Integer filasAfectadas = conexionDAO.delete(idConexion);
			
			return new JSONObject()
					.put("status", "success")
					.put("message", "Conexión eliminada correctamente.")
					.put("filasAfectadas", filasAfectadas);
					
		} catch (Exception e) {
			System.err.println("Aviso BD: No se pudo eliminar la conexión ID " + idConexion + ". Detalle: " + e.getMessage());
			
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

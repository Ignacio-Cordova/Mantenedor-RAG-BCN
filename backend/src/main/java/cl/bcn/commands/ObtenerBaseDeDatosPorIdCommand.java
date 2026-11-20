package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.BaseDeDatos;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Obtiene una fuente PostgreSQL por id y serializa sus relaciones mediante BaseDeDatos.toJSON().
 */
public class ObtenerBaseDeDatosPorIdCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));		
		validateRule(validator.validateInt("id"));
	}

	@Override
	protected JSONObject execute() throws Exception {
		Integer id = getIntParam("id");
		BaseDeDatos bd = getDAOFactory().getBaseDeDatosDAO().findById(id);

		if (bd == null) {
		    return new JSONObject().put("error", "No se encontró la base de datos con el ID proporcionado.");
		}
		return bd.toJSON();
	}

}

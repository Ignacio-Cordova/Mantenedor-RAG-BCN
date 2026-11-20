package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.BaseDeDatos;
import cl.bcn.persistence.dao.BaseDeDatosDAO;

import java.util.List;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Lista las fuentes de base de datos disponibles mediante BaseDeDatos.toJSON().
 */
public class ObtenerBasesDeDatosCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONArray execute() throws Exception {
		BaseDeDatosDAO dao = getDAOFactory().getBaseDeDatosDAO();
		List<BaseDeDatos> basesDeDatos = dao.findAll();
		JSONArray jsonArray = new JSONArray();
	    for (BaseDeDatos bdd : basesDeDatos) {
	        jsonArray.put(bdd.toJSON()); 
	    }
		return jsonArray;
	}

	@Override
	protected void validateParams() throws Exception {
	}

}

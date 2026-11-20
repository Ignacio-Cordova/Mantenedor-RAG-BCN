package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;

import org.json.JSONArray;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Conexion;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ConexionDAO;

import java.util.List;
/**
 * Lista todas las conexiones PostgreSQL mediante Conexion.toJSON().
 */
public class ObtenerConexionesCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONArray execute() throws Exception {
		ConexionDAO dao = getDAOFactory().getConexionDAO();
		List<Conexion> conexiones = dao.findAll();
		JSONArray jsonArray = new JSONArray();
	    for (Conexion ds : conexiones) {
	        jsonArray.put(ds.toJSON()); 
	    }
	    
	    return jsonArray;
	}

	@Override
	protected void validateParams() throws Exception {
	}

}
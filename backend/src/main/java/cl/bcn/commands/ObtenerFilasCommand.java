package cl.bcn.commands;
import cl.bcn.model.BaseDeDatos;
import java.util.Date;
import org.json.JSONObject;

import java.util.List;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.util.ExtractorJDBC;
/**
 * Previsualiza hasta cuatro filas de una fuente JDBC según query o tabla y columna de contenido.
 */
public class ObtenerFilasCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		BaseDeDatos bd = new BaseDeDatos();
		bd.setColumnaContenido(getParam("columnaContenido"));
		bd.setConexion(getDAOFactory().getConexionDAO().findById(getIntParam("idConexion")));
		bd.setCrdate(new Date());
		bd.setTablaObjetivo(getParam("tablaObjetivo"));
		bd.setQuerySql(getParam("query"));
		bd.setModoExtraccion(getParam("modoExtraccion"));
		bd.setNombre(getParam("nombre"));
		bd.setTipo(getParam("tipo"));
		List<String> filas = ExtractorJDBC.extraerTextos(bd, 4);
		
		JSONArray jsonArray = new JSONArray();
		if (filas != null) {
			for (String fila : filas) {
				jsonArray.put(fila); 
			}
		}
		return new JSONObject()
				.put("status", "success")
				.put("data", jsonArray);
	}


	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("idConexion"));
		validateRule(validator.validateNotEmpty("tablaObjetivo"));
		validateRule(validator.validateNotEmpty("query"));
		validateRule(validator.validateNotEmpty("modoExtraccion"));
		validateRule(validator.validateNotEmpty("columnaContenido"));
		validateRule(validator.validateNotEmpty("nombre"));
		validateRule(validator.validateNotEmpty("tipo"));
		
		validateRule(validator.validateInt("idConexion"));		
	}

}

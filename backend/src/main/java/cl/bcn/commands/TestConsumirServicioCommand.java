package cl.bcn.commands;
import cl.bcn.model.Api;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.commands.params.FormDataParams;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Ejecuta una prueba técnica contra el servicio REST fijo de Ley Fácil y devuelve su respuesta.
 */
public class TestConsumirServicioCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService, FormDataParams {

	@Override
	protected void validateParams() throws Exception {
	}

	@Override
	protected JSONObject execute() throws Exception {
		String url = "https://newbcn.bcn.cl/api-busqueda/servicios/buscar.py?&npagina=1&rows=50&tipo_recurso=Ley%20F%C3%A1cil&orden_por=2&orden_ad=desc&camara=";
		String out = UTILS.getHTTPUtil().executeServiceGet(url, MediaType.APPLICATION_JSON_TYPE);
		JSONObject o = new JSONObject(out);
		
		o.getJSONArray("docs");
		for (int i = 0; i < o.getJSONArray("docs").length(); i++) {
			JSONObject doc = o.getJSONArray("docs").getJSONObject(i);
		}
		
		return o;
	}
}

package cl.bcn.commands;
import cacatua.configuration.ManagerConfiguracion;
import cacatua.business.method.HTTPGetService;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Recarga configuración y devuelve versión, build, instancia y estado del servicio.
 */
public class VersionCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
	}

	@Override
	protected JSONObject execute() throws Exception {
		JSONObject o = getOkDefaultResult();
		LOG.info("Configuracion recargada=" + ManagerConfiguracion.recargarConfiguracion());
		o.put("ver", ManagerConfiguracion.getString("version"));
		o.put("build", ManagerConfiguracion.getString("build"));
		o.put("servicio", "ok");
		o.put("in", ManagerConfiguracion.getInstanceNumber());
		if("Desarrollo".equals(ManagerConfiguracion.getString("entorno"))) {
			o.put("config", new JSONObject(ManagerConfiguracion.getAllConfiguration()));
		}
		o.put("test", getParam("test"));
		return o;
	}
}

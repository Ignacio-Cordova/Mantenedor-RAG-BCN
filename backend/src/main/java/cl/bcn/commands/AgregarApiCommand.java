package cl.bcn.commands;
import java.util.Date;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Api;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Registra un proveedor de API con nombre, URL base, organización, autenticación y clave encriptada.
 */
public class AgregarApiCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		String nombre_proveedor = getParam("nombre_proveedor");
		String base_url = getParam("base_url");
		String organizacion_id = getParam("organizacion_id");
		String tipo_autenticacion = getParam("tipo_autenticacion");
		String api_key_encriptada = getParam("api_key_encriptada");
		
		Api api = new Api();
		api.setNombreProveedor(nombre_proveedor);
		api.setBaseUrl(base_url);
		api.setOrganizacionId(organizacion_id);
		api.setTipoAutenticacion(tipo_autenticacion);
		api.setApiKeyEncriptada(api_key_encriptada);
		api.setCrdate(new java.util.Date());
		
		getDAOFactory().getApiDAO().insert(api);
		
		JSONObject response = new JSONObject();
		response.put("success", true);
		response.put("message", "API agregada exitosamente");
		return response;
		
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("nombre_proveedor"));
		validateRule(validator.validateNotEmpty("base_url"));
		validateRule(validator.validateNotEmpty("organizacion_id"));
		validateRule(validator.validateNotEmpty("tipo_autenticacion"));
		validateRule(validator.validateNotEmpty("api_key_encriptada"));
		validateRule(validator.validateRequired("nombre_proveedor"));
	}

}

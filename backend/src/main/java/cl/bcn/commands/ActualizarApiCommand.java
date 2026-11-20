package cl.bcn.commands;
import cl.bcn.persistence.dao.ApiDAO;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Api;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Actualiza un proveedor de API identificado por id; modifica URL base, organización, autenticación y clave en PostgreSQL.
 */
public class ActualizarApiCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService{

	@Override
	protected JSONObject execute() throws Exception {
		Integer id = getIntParam("id");
		ApiDAO dao = getDAOFactory().getApiDAO();
		
		Api api = dao.findById(id);
		
		if (api == null) {
			throw new Exception("Api no encontrada con id: " + id);
		}
		
		api.setNombreProveedor(getParam("nombre_proveedor"));
		api.setBaseUrl(getParam("base_url"));
		api.setOrganizacionId(getParam("organizacion_id"));
		api.setTipoAutenticacion(getParam("tipo_autenticacion"));
		api.setApiKeyEncriptada(getParam("api_key_encriptada"));
		
		dao.update(api);
		
		return new JSONObject().put("status", "success");
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
	}

}

package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.ModeloLlm;
import java.util.Date;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloLlmDAO;
/**
 * Registra un modelo LLM con nombre técnico, límite de tokens, endpoint, plantilla JSON, ruta de respuesta y API.
 */
public class AgregarModeloLlmCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("nombre"));
		validateRule(validator.validateNotEmpty("nombre_tecnico"));
		validateRule(validator.validateInt("limite_tokens"));
		validateRule(validator.validateInt("id_api"));
	}

	@Override
	protected JSONObject execute() throws Exception {
		String nombre = getParam("nombre");
		String nombre_tecnico = getParam("nombre_tecnico");
		Integer limite_tokens = getIntParam("limite_tokens");
		String endpoint = getParam("endpoint");
		String plantilla_payload = getParam("plantilla_payload");
		String ruta_respuesta = getParam("ruta_respuesta");
		Integer id_api = getIntParam("id_api");
	
		ModeloLlm modeloLlm = new ModeloLlm();
		modeloLlm.setNombre(nombre);
		modeloLlm.setNombreTecnico(nombre_tecnico);
		modeloLlm.setLimiteTokens(limite_tokens);
		modeloLlm.setEndpoint(endpoint);
		modeloLlm.setPlantillaPayload(plantilla_payload);
		modeloLlm.setRutaRespuesta(ruta_respuesta);
		modeloLlm.setApi(getDAOFactory().getApiDAO().findById(id_api));
		modeloLlm.setCrdate(new java.util.Date());
		
		ModeloLlmDAO modeloLlmDAO = getDAOFactory().getModeloLlmDAO();
		
		modeloLlmDAO.insert(modeloLlm);
		
		JSONObject response = new JSONObject();
		response.put("success", true);
		response.put("message", "Modelo LLM agregado exitosamente");
		return response;
		
	}

}

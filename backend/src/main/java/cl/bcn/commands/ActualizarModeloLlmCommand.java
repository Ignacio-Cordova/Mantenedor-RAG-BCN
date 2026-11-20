package cl.bcn.commands;
import cl.bcn.model.ModeloLlm;
import cl.bcn.persistence.dao.ApiDAO;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Api;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ModeloLlmDAO;
/**
 * Actualiza por id la plantilla técnica de un modelo LLM: nombre, endpoint, payload, ruta de respuesta y API asociada.
 */
public class ActualizarModeloLlmCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		Integer id = getIntParam("id");
		ModeloLlmDAO dao = getDAOFactory().getModeloLlmDAO();
		ApiDAO daoApi = getDAOFactory().getApiDAO();
		ModeloLlm modeloLlm = dao.findById(id);
		
		if (modeloLlm == null) {
			throw new Exception("Modelo LLM no encontrado con id: " + id);
		}
		
		modeloLlm.setNombre(getParam("nombre"));
		modeloLlm.setNombreTecnico(getParam("nombre_tecnico"));
		modeloLlm.setLimiteTokens(getIntParam("limite_tokens"));
		modeloLlm.setEndpoint(getParam("endpoint"));
		modeloLlm.setPlantillaPayload(getParam("plantilla_payload"));
		modeloLlm.setRutaRespuesta(getParam("ruta_respuesta"));
		modeloLlm.setApi(daoApi.findById(getIntParam("id_api")));
		
		dao.update(modeloLlm);
		
		return new JSONObject().put("status", "success");
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
	}

}

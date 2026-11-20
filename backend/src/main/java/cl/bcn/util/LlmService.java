package cl.bcn.util;
import cl.bcn.model.ModeloLlm;
import cl.bcn.model.Api;
import org.json.JSONObject;

/**
 * Construye las solicitudes HTTP para los modelos LLM configurados.
 * Resuelve endpoint, payload dinámico y ruta de extracción de la respuesta desde PostgreSQL.
 */

public class LlmService {
	
	public String generarRespuesta(String prompt, ModeloLlm modelo) throws Exception {
		String urlBase = modelo.getApi().getBaseUrl(); 
		String endpoint = modelo.getEndpoint(); 
		if (urlBase != null && urlBase.endsWith("/")) {
		    urlBase = urlBase.substring(0, urlBase.length() - 1);
		}
		if (endpoint != null && !endpoint.trim().isEmpty()) {
		    if (!endpoint.startsWith("/")) {
		        endpoint = "/" + endpoint;
		    }
		} else {
		    endpoint = "/api/generate";
		}
		
		String URL = urlBase + endpoint;
		String payloadFinal;
		String plantilla = modelo.getPlantillaPayload();
		
		if (plantilla != null && !plantilla.trim().isEmpty()) {
			String promptEscapado = JSONObject.quote(prompt); 
			promptEscapado = promptEscapado.substring(1, promptEscapado.length() - 1);
			payloadFinal = plantilla.replace("{{nombre_modelo}}", modelo.getNombreTecnico())
			                        .replace("{{prompt_ensamblado}}", promptEscapado);
		} else {
			JSONObject payloadObj = new JSONObject();
			payloadObj.put("model", modelo.getNombreTecnico());
			payloadObj.put("stream", false);
			payloadObj.put("prompt", prompt);
			payloadFinal = payloadObj.toString();
		}
		String response = HTTPService.sendPostRequest(URL, payloadFinal);
		JSONObject respuestaJson = new JSONObject(response);
		String textoFinal = "";
		String rutaRespuesta = modelo.getRutaRespuesta(); 
		
		if (rutaRespuesta != null && !rutaRespuesta.trim().isEmpty()) {
			Object valorExtraido = ExtractorServicioWeb.obtenerValorAnidado(respuestaJson, rutaRespuesta);
			
			if (valorExtraido != null) {
				textoFinal = String.valueOf(valorExtraido);
			} else {
				throw new Exception("El LLM respondió, pero no se encontró la ruta '" + rutaRespuesta + "' en el JSON.");
			}
		} else {
			textoFinal = respuestaJson.getString("response");
		}
		
		return textoFinal;
	}
}
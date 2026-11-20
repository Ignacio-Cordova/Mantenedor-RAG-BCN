package cl.bcn.commands;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.util.GroqJudgeService;

import java.io.BufferedReader;
/**
 * Recibe pregunta, contexto y respuesta, e invoca el juez Groq para calificar fidelidad y relevancia.
 */
public class EvaluarJuezCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

    private GroqJudgeService juezService;

    public EvaluarJuezCommand() {
        this.juezService = new GroqJudgeService();
    }

    @Override
    public JSONObject execute() {
        try {
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = request.getReader();
            String linea;
            while ((linea = reader.readLine()) != null) {
                buffer.append(linea);
            }
            
            JSONObject jsonPeticion = new JSONObject(buffer.toString());
            String pregunta = jsonPeticion.getString("pregunta");
            String contextoRecuperado = jsonPeticion.getString("contextoRecuperado");
            String respuestaGemma = jsonPeticion.getString("respuestaGemma");
            String promptFidelidad =
            	    "Eres un juez auditor experto en derecho chileno y sistemas RAG (LLM-as-a-Judge). " +
            	    "Tu única tarea es calificar la FIDELIDAD AL CONTEXTO de la 'Respuesta Generada' en una escala entera de 1 a 4, " +
            	    "evaluando si la información proviene estrictamente del 'Contexto Recuperado' sin alucinar.\n\n" +
            	    "REGLAS ESTRICTAS DE CALIFICACIÓN (FIDELIDAD):\n" +
            	    "- Puntaje 4 (Totalmente Respaldada): Todos los datos sustantivos provienen del contexto recuperado. " +
            	    "IMPORTANTE: (a) Si la respuesta se abstiene diciendo únicamente 'Lo siento, no tengo suficiente información en la normativa consultada para responder a esto' porque la guía consultada no está en el contexto, DEBES calificar con 4 (no alucina). " +
            	    "(b) Si la respuesta entrega primero la información correcta basada en el primer fragmento del contexto y al final agrega la coletilla 'Lo siento, no tengo suficiente información...' refiriéndose al segundo fragmento ajeno, IGNORA esa coletilla final y califica con 4. " +
            	    "(c) Si la respuesta incluye detalles adicionales o ejemplos que SÍ aparecen en el mismo fragmento del contexto, califica con 4.\n" +
            	    "- Puntaje 3 (Parcialmente Respaldada): La respuesta principal es fiel al contexto, pero añade algún dato menor no presente en los fragmentos o mezcla parcialmente información de un segundo fragmento sin contradecir la idea central.\n" +
            	    "- Puntaje 2 (No Respaldada): El modelo inventa números de leyes que NO aparecen escritos en el contexto recuperado (alucinación), o bien el contexto NO contiene la guía normativa consultada y el modelo, en vez de abstenerse, responde explicando reglas de otra ley distinta del contexto como si respondieran la pregunta.\n" +
            	    "- Puntaje 1 (Contradictoria): Contradice directamente el contexto o define el concepto preguntado usando la definición de un bono, registro o institución totalmente distinto del contexto.\n\n" +
            	    "Responde ÚNICAMENTE con un objeto JSON válido con el formato: {\"puntaje\": <numero_entero_1_a_4>}";
            String promptRelevancia =
            	    "Eres un juez auditor experto en derecho chileno y sistemas RAG (LLM-as-a-Judge). " +
            	    "Tu única tarea es calificar la RELEVANCIA (PERTINENCIA) DE LA RESPUESTA frente a la 'Pregunta del Usuario' en una escala entera de 1 a 4.\n" +
            	    "IMPORTANTE: Evalúa estrictamente la PERTINENCIA (correspondencia directa con la pregunta) y NO la exhaustividad o longitud del texto. No penalices respuestas concisas o resumidas si responden directamente lo preguntado.\n\n" +
            	    "REGLAS ESTRICTAS DE CALIFICACIÓN (RELEVANCIA):\n" +
            	    "- Puntaje 4 (Totalmente Pertinente): La respuesta ataca de forma directa, clara y específica la pregunta del usuario según la guía normativa consultada, ya sea de forma breve o detallada. " +
            	    "Si la respuesta resuelve la duda en sus primeras oraciones y al final incluye la coletilla 'Lo siento, no tengo suficiente información en la normativa consultada para responder a esto' (por descartar un segundo bloque ajeno), IGNORA dicha coletilla y califica con 4.\n" +
            	    "- Puntaje 3 (Parcialmente Pertinente): La respuesta resuelve la pregunta del usuario, pero añade párrafos tangenciales sobre otros trámites no consultados o incluye texto crudo de etiquetas de metadatos.\n" +
            	    "- Puntaje 2 (Poco Pertinente): El contexto recuperado NO contiene la guía normativa consultada y el modelo responde hablando de otra ley tangencial sin resolver la duda específica del usuario.\n" +
            	    "- Puntaje 1 (No Pertinente): El modelo responde únicamente con la frase de abstención ('Lo siento, no tengo suficiente información en la normativa consultada para responder a esto'), o responde definiendo un beneficio o institución completamente distinto al preguntado.\n\n" +
            	    "Responde ÚNICAMENTE con un objeto JSON válido con el formato: {\"puntaje\": <numero_entero_1_a_4>}";
            String rawFidelidad = juezService.evaluarConJuez(
                    promptFidelidad, pregunta, contextoRecuperado,
                    "No aplica...", respuestaGemma
            );
            JSONObject jsonFidelidad = parsearRespuestaJuez(rawFidelidad);
            if (jsonFidelidad.has("error")) return jsonFidelidad;

            try {
                Thread.sleep(35000); 
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            String rawRelevancia = juezService.evaluarConJuez(
                    promptRelevancia, pregunta, contextoRecuperado,
                    "No aplica", respuestaGemma
            );
            JSONObject jsonRelevancia = parsearRespuestaJuez(rawRelevancia);
            if (jsonRelevancia.has("error")) {
                return jsonRelevancia;
            }
            JSONObject resultadoFinal = new JSONObject();
            resultadoFinal.put("Fidelidad", jsonFidelidad.getInt("puntaje"));
            resultadoFinal.put("Relevancia", jsonRelevancia.getInt("puntaje"));

            return resultadoFinal;

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Ocurrió un error en la evaluación: " + e.getMessage());
            return errorJson;
        }
    }

    /**
     * Limpia posibles bloques Markdown (```json ... ```) y valida errores HTTP de Groq.
     */
    private JSONObject parsearRespuestaJuez(String respuestaRaw) {
        if (respuestaRaw == null || respuestaRaw.startsWith("Error") || respuestaRaw.startsWith("Excepción")) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", respuestaRaw != null ? respuestaRaw : "Respuesta nula del servicio Groq");
            return errorJson;
        }

        String jsonLimpio = respuestaRaw.trim();
        if (jsonLimpio.startsWith("```json")) {
            jsonLimpio = jsonLimpio.substring(7);
        } else if (jsonLimpio.startsWith("```")) {
            jsonLimpio = jsonLimpio.substring(3);
        }
        if (jsonLimpio.endsWith("```")) {
            jsonLimpio = jsonLimpio.substring(0, jsonLimpio.length() - 3);
        }

        return new JSONObject(jsonLimpio.trim());
    }

    @Override
    protected void validateParams() throws Exception {
    }
}
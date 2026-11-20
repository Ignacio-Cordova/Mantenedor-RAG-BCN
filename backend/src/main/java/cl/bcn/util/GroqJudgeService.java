package cl.bcn.util;
import cl.bcn.model.Api;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
/**
 * Invoca el modelo juez de Groq y administra la rotación de credenciales ante límites de cuota.
 * Envía la pregunta, el contexto recuperado y la respuesta generada para una evaluación determinista.
 */

public class GroqJudgeService {

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL_NAME = "openai/gpt-oss-120b";

    private static final String[] GROQ_API_KEYS = {
        "sk-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
    };
    private static int indiceKeyActual = 0;

    /**
     * Evalúa una dimensión del sistema RAG enviando el 100% del contexto recuperado real.
     */
    public String evaluarConJuez(String systemPromptRubrica, String pregunta, String contextoRecuperado, String respuestaEsperada, String respuestaGemma) {
        int intentosRotacion = 0;

        while (intentosRotacion < GROQ_API_KEYS.length) {
            String apiKeyActual = GROQ_API_KEYS[indiceKeyActual];
            try {
                URL url = new URL(GROQ_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + apiKeyActual);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String contenidoUsuario = "PREGUNTA DEL CIUDADANO:\n" + pregunta + "\n\n" +
                                          "CONTEXTO RECUPERADO (Fragmentos inyectados desde Qdrant):\n" + contextoRecuperado + "\n\n" +
                                          "RESPUESTA OFICIAL BCN (Referencia):\n" + respuestaEsperada + "\n\n" +
                                          "RESPUESTA GENERADA (Gemma 3 RAG):\n" + respuestaGemma + "\n\n" +
                                          "Basado estrictamente en la rúbrica de 1 a 4 definida en el System Prompt, evalúa la RESPUESTA GENERADA (puntaje máximo permitido: 4).";

                JSONObject data = new JSONObject();
                data.put("model", MODEL_NAME);
                data.put("temperature", 0.0);

                JSONArray messages = new JSONArray();

                JSONObject systemMessage = new JSONObject();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPromptRubrica);
                messages.put(systemMessage);

                JSONObject userMessage = new JSONObject();
                userMessage.put("role", "user");
                userMessage.put("content", contenidoUsuario);
                messages.put(userMessage);

                data.put("messages", messages);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = data.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                } else {
                    InputStream es = conn.getErrorStream();
                    StringBuilder detalleError = new StringBuilder();
                    if (es != null) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(es, "utf-8"));
                        String line;
                        while ((line = br.readLine()) != null) {
                            detalleError.append(line.trim());
                        }
                    }
                    String msgError = detalleError.toString();
                    if (responseCode == 429 && (msgError.contains("tokens per day") || msgError.contains("TPD") || GROQ_API_KEYS.length > 1)) {
                        indiceKeyActual = (indiceKeyActual + 1) % GROQ_API_KEYS.length;
                        intentosRotacion++;
                        continue;
                    }
                    return "Error HTTP en la API de Groq: Código " + responseCode + " - " + msgError;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Excepción al conectar con el juez automático: " + e.getMessage();
            }
        }
        return "Error HTTP en la API de Groq: Código 429 (Todas las API Keys alcanzaron el límite)";
    }
}
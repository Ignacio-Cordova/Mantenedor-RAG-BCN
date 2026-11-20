package cl.bcn.util;
import cl.bcn.model.Api;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import cl.bcn.model.ModeloEmbedding;
/**
 * Obtiene vectores densos y dispersos para indexación y consulta.
 * Los densos usan el modelo Embedding asociado al dataset y los dispersos se generan mediante el servicio SPLADE.
 */

public class EmbeddingService {
    public static class VectorDisperso {
        private final List<Integer> indices;
        private final List<Float> valores;

        public VectorDisperso(List<Integer> indices, List<Float> valores) {
            this.indices = indices;
            this.valores = valores;
        }

        public List<Integer> getIndices() { return indices; }
        public List<Float> getValores() { return valores; }
    }

    public List<Double> obtenerVector(ModeloEmbedding modeloEmbedding, String texto) throws Exception {
        
        // 1. CORRECCIÓN: El endpoint oficial nuevo es /api/embed
        String API_URL = modeloEmbedding.getApi().getBaseUrl() + "/api/embed";
        
        String MODEL_NAME = modeloEmbedding.getNombreTecnico();
        JSONObject payload = new JSONObject();
        payload.put("model", MODEL_NAME);
        
        // 2. CORRECCIÓN: Ollama ahora espera "input" en lugar de "prompt"
        payload.put("input", texto);
        
        String response = HTTPService.sendPostRequest(API_URL, payload.toString());
        JSONObject jsonResponse = new JSONObject(response.toString());
        
        JSONArray embeddingArray;
        // Tu lógica de parseo aquí está perfecta para la nueva API
        if (jsonResponse.has("embeddings")) {
            embeddingArray = jsonResponse.getJSONArray("embeddings").getJSONArray(0);
        } else {
            embeddingArray = jsonResponse.getJSONArray("embedding");
        }
        
        List<Double> vector = new ArrayList<>();
        for (int i = 0; i < embeddingArray.length(); i++) {
            vector.add(embeddingArray.getDouble(i));
        }
        
        return vector;
    }

    public VectorDisperso obtenerVectorDisperso(String texto) throws Exception {
        String API_URL = "http://rag_splade:8000/splade";
        
        JSONObject payload = new JSONObject();
        payload.put("text", texto);
        
        String response = HTTPService.sendPostRequest(API_URL, payload.toString());
        JSONObject jsonResponse = new JSONObject(response);
        
        JSONArray indicesArray = jsonResponse.getJSONArray("indices");
        JSONArray valoresArray = jsonResponse.getJSONArray("values");
        
        List<Integer> indices = new ArrayList<>();
        List<Float> valores = new ArrayList<>();
        
        for (int i = 0; i < indicesArray.length(); i++) {
            indices.add(indicesArray.getInt(i));
        }
        for (int i = 0; i < valoresArray.length(); i++) {
            valores.add((float) valoresArray.getDouble(i));
        }
        
        return new VectorDisperso(indices, valores);
    }
}
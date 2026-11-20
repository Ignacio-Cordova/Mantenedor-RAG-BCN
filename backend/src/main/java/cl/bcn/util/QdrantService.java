package cl.bcn.util;
import cl.bcn.model.Documento;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
/**
 * Administra colecciones y puntos Qdrant mediante HTTP nativo.
 * Conserva IDs deterministas y permite eliminación por documento_id para compensar PostgreSQL.
 */

public class QdrantService {

    private static final String QDRANT_URL = "http://qdrant:6333"; 
    
    public void eliminarPorIdDocumento(String collectionName, Integer idDocumentoPostgres) throws Exception {
        String endpoint = QDRANT_URL + "/collections/" + collectionName + "/points/delete?wait=true";
        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST"); 
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        JSONObject match = new JSONObject();
        match.put("value", idDocumentoPostgres);

        JSONObject condition = new JSONObject();
        condition.put("key", "documento_id");
        condition.put("match", match);

        JSONArray mustArray = new JSONArray();
        mustArray.put(condition);

        JSONObject filter = new JSONObject();
        filter.put("must", mustArray);
        JSONObject body = new JSONObject();
        body.put("filter", filter);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int status = con.getResponseCode();
        if (status != 200) {
            Scanner s = new Scanner(con.getErrorStream()).useDelimiter("\\A");
            String error = s.hasNext() ? s.next() : "";
            throw new Exception("Error al borrar en Qdrant (" + status + "): " + error);
        }
    }  

    public void crearColeccionSiNoExiste(String collectionName, int dimension, boolean esHibrida) {
        try {
            URL url = new URL(QDRANT_URL + "/collections/" + collectionName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            JSONObject config = new JSONObject();

            if (esHibrida) {
                JSONObject vectors = new JSONObject();
                JSONObject textDense = new JSONObject();
                textDense.put("size", dimension);
                textDense.put("distance", "Cosine");
                vectors.put("text-dense", textDense);
                
                JSONObject sparseVectors = new JSONObject();
                sparseVectors.put("text-sparse", new JSONObject()); 
                
                config.put("vectors", vectors);
                config.put("sparse_vectors", sparseVectors);
            } else {
                JSONObject vectors = new JSONObject();
                vectors.put("size", dimension);
                vectors.put("distance", "Cosine");
                config.put("vectors", vectors);
            }
            
            try (OutputStream os = con.getOutputStream()) {
                os.write(config.toString().getBytes("utf-8"));
            }
            
            int status = con.getResponseCode();
            if (status == 200) {
            } else {
            }
        } catch (Exception e) {
            System.err.println("Error intentando crear colección: " + e.getMessage());
        }
    }
    
    public void upsert(String nombreColeccion, List<Double> vector, String textoChunk, Integer idDocumentoOriginal, int chunkIndex, JSONObject metadatos) throws Exception {
        String endpoint = QDRANT_URL + "/collections/" + nombreColeccion + "/points?wait=true";
        URL url = new URL(endpoint);
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        JSONObject point = new JSONObject();
        
        String stringId = idDocumentoOriginal + "_" + chunkIndex;
        String idDeterminista = java.util.UUID.nameUUIDFromBytes(stringId.getBytes(StandardCharsets.UTF_8)).toString();
        
        point.put("id", idDeterminista);
        point.put("vector", new JSONArray(vector));
        JSONObject payload = new JSONObject();
        
        payload.put("texto", textoChunk);
        payload.put("documento_id", idDocumentoOriginal != null ? idDocumentoOriginal : -1);
        payload.put("chunk_index", chunkIndex);
        if (metadatos != null) {
            for (String key : metadatos.keySet()) {
                payload.put(key, metadatos.get(key));
            }
        }
        
        point.put("payload", payload);
        JSONObject body = new JSONObject();
        JSONArray pointsArray = new JSONArray();
        pointsArray.put(point);
        body.put("points", pointsArray);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int status = con.getResponseCode();
        if (status != 200) {
            Scanner s = new Scanner(con.getErrorStream()).useDelimiter("\\A");
            String error = s.hasNext() ? s.next() : "";
            throw new Exception("Error Qdrant (" + status + "): " + error);
        }
    }
    
    public void upsertHibrido(String nombreColeccion, String idDocumento, 
            List<Float> vectorDenso, 
            List<Integer> indicesDispersos, 
            List<Float> valoresDispersos, 
            Map<String, Object> metadatos) throws Exception {

        String endpoint = QDRANT_URL + "/collections/" + nombreColeccion + "/points?wait=true";
        URL url = new URL(endpoint);
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        JSONObject vectores = new JSONObject();
        vectores.put("text-dense", new JSONArray(vectorDenso));
        JSONObject vectorDisperso = new JSONObject();
        vectorDisperso.put("indices", new JSONArray(indicesDispersos));
        vectorDisperso.put("values", new JSONArray(valoresDispersos));
        vectores.put("text-sparse", vectorDisperso);
        JSONObject punto = new JSONObject();
        
        punto.put("id", idDocumento); 
        punto.put("vector", vectores);
        punto.put("payload", new JSONObject(metadatos));
        JSONArray pointsArray = new JSONArray();
        pointsArray.put(punto);
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("points", pointsArray);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        int status = con.getResponseCode();
        if (status != 200) {
            Scanner s = new Scanner(con.getErrorStream()).useDelimiter("\\A");
            String error = s.hasNext() ? s.next() : "";
            throw new Exception("Error Qdrant (" + status + "): " + error);
        }
    } 
    
    public void eliminarColeccion(String collectionName) throws Exception {
        String endpoint = QDRANT_URL + "/collections/" + collectionName;
        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Accept", "application/json");
        int status = con.getResponseCode();
        if (status != 200 && status != 404) {
            Scanner s = new Scanner(con.getErrorStream()).useDelimiter("\\A");
            String error = s.hasNext() ? s.next() : "";
            throw new Exception("Error al eliminar la colección en Qdrant (" + status + "): " + error);
        }
    }
    
    public List<String> buscar(String collectionName, List<Double> vectorConsulta, int topK) throws Exception {
        String qdrantURL = QDRANT_URL + "/collections/" + collectionName + "/points/search";
        JSONObject busqueda = new JSONObject();
        busqueda.put("vector", vectorConsulta);
        busqueda.put("limit", topK);
        busqueda.put("with_payload", true);
        
        String jsonPayload = busqueda.toString();
        
        List<String> textosExtraidos = new ArrayList<>();
        
        try {
            
            String jsonRespuestaQdrant = HTTPService.sendPostRequest(qdrantURL, jsonPayload);
            JSONObject respuestaJSON = new JSONObject(jsonRespuestaQdrant);
            JSONArray resultadosArray = respuestaJSON.getJSONArray("result");
            
            for (int i= 0 ; i < resultadosArray.length(); i++ ) {
                JSONObject resultado = resultadosArray.getJSONObject(i);
                JSONObject payload = resultado.getJSONObject("payload");
                
                String textoChunk = null;
                if (payload.has("texto") && !payload.isNull("texto")) {
                    textoChunk = payload.getString("texto");
                } else if (payload.has("page_content") && !payload.isNull("page_content")) {
                    textoChunk = payload.getString("page_content");
                } 
                
                if (textoChunk != null) {
                    textosExtraidos.add(formatearChunkAgnostico(payload, textoChunk));
                } else {
                    System.err.println("Advertencia: Se omitió un chunk en la recuperación porque no posee la llave 'page_content' ni 'texto'.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar en Qdrant: " + e.getMessage());
            throw e;
        }
        
        return textosExtraidos;
    }
    
    public List<String> buscarHibrido(String collectionName, List<Double> vectorDensoConsulta, List<Integer> indicesDispersos, List<Float> valoresDispersos, int topK) throws Exception {
        
        String qdrantURL = QDRANT_URL + "/collections/" + collectionName + "/points/query";
        
        JSONObject busqueda = new JSONObject();
        JSONArray prefetchArray = new JSONArray();
        JSONObject prefetchDenso = new JSONObject();
        prefetchDenso.put("using", "text-dense"); 
        prefetchDenso.put("query", new JSONArray(vectorDensoConsulta));
        prefetchDenso.put("limit", topK * 2);
        prefetchArray.put(prefetchDenso);
        JSONObject prefetchDisperso = new JSONObject();
        JSONObject vectorDisperso = new JSONObject();
        vectorDisperso.put("indices", new JSONArray(indicesDispersos));
        vectorDisperso.put("values", new JSONArray(valoresDispersos));
        
        prefetchDisperso.put("using", "text-sparse");
        prefetchDisperso.put("query", vectorDisperso);
        prefetchDisperso.put("limit", topK * 2);
        prefetchArray.put(prefetchDisperso);
        
        busqueda.put("prefetch", prefetchArray);
        JSONObject queryFusion = new JSONObject();
        queryFusion.put("fusion", "rrf");
        
        busqueda.put("query", queryFusion);
        busqueda.put("limit", topK);
        busqueda.put("with_payload", true);
        
        String jsonPayload = busqueda.toString();
        List<String> textosExtraidos = new ArrayList<>();
        
        try {
            String jsonRespuestaQdrant = HTTPService.sendPostRequest(qdrantURL, jsonPayload);
            
            JSONObject respuestaJSON = new JSONObject(jsonRespuestaQdrant);
            JSONArray resultadosArray = respuestaJSON.getJSONObject("result").getJSONArray("points");
            
            for (int i= 0 ; i < resultadosArray.length(); i++ ) {
                JSONObject resultado = resultadosArray.getJSONObject(i);
                JSONObject payload = resultado.getJSONObject("payload");
                
                String textoChunk = null;
                if (payload.has("texto") && !payload.isNull("texto")) {
                    textoChunk = payload.getString("texto");
                } else if (payload.has("page_content") && !payload.isNull("page_content")) {
                    textoChunk = payload.getString("page_content");
                } 
                
                if (textoChunk != null) {
                    StringBuilder chunkFormateado = new StringBuilder();
                    
                    // Iterar dinámicamente sobre TODOS los metadatos que traiga Qdrant
                    for (String key : payload.keySet()) {
                        // Ignoramos la llave principal del texto para no duplicarla
                        if (!key.equals("texto") && !key.equals("page_content") && !payload.isNull(key)) {
                            String valor = payload.get(key).toString();
                            
                            // Formateo dinámico: [NombreDelMetadato: Valor]
                            // Capitalizamos la primera letra de la llave para que se vea más limpio (ej: [Materias: ...] o [Autor: ...])
                            String keyCapitalizada = key.substring(0, 1).toUpperCase() + key.substring(1).replace("_", " ");
                            chunkFormateado.append("[").append(keyCapitalizada).append(": ").append(valor).append("]\n");
                        }
                    }
                    
                    chunkFormateado.append("Contenido: ").append(textoChunk);
                    textosExtraidos.add(chunkFormateado.toString());
                } else {
                    System.err.println("Advertencia: Se omitió un chunk porque no posee la llave de texto.");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al buscar en colección híbrida Qdrant: " + e.getMessage());
            throw e;
        }
        
        return textosExtraidos;
    }
    
    private String formatearChunkAgnostico(JSONObject payload, String textoChunk) {
        StringBuilder sb = new StringBuilder();
        if (payload.has("titulo") || payload.has("uri")) {
            String titulo = payload.optString("titulo", "Documento");
            String uri = payload.optString("uri", "");
            String etiqueta = uri.isEmpty() ? titulo : titulo + " (" + uri + ")";
            sb.append("[Guía/Norma: ").append(etiqueta).append("]\n");
        }
        for (String key : payload.keySet()) {
            if (key.equals("texto") || key.equals("page_content") || 
                key.equals("documento_id") || key.equals("chunk_index") ||
                key.equals("titulo") || key.equals("uri")) {
                continue;
            }

            Object valor = payload.opt(key);
            if (valor != null && !valor.toString().trim().isEmpty()) {
                sb.append("[").append(key).append(": ").append(valor.toString().trim()).append("]\n");
            }
        }

        sb.append("Contenido: ").append(textoChunk);
        return sb.toString();
    }
}
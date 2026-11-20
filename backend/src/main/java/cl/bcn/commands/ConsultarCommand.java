package cl.bcn.commands;

import cl.bcn.model.Dataset;
import cl.bcn.model.RetrievalMethod;
import org.json.JSONObject;

import java.util.List;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DatasetDAO;
import cl.bcn.util.EmbeddingService;
import cl.bcn.util.LlmService;
import cl.bcn.util.QdrantService;

/**
 * Vectoriza la consulta con los modelos configurados, recupera contexto denso o híbrido y genera la respuesta del LLM.
 */
public class ConsultarCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

    @Override
    protected JSONObject execute() throws Exception {
        DatasetDAO datasetDAO = getDAOFactory().getDatasetDAO();
            
        QdrantService qdrantService = new QdrantService();
        EmbeddingService embeddingService = new EmbeddingService();
        LlmService llmService = new LlmService();
        
        JSONObject respuesta = new JSONObject();
        
        Integer id = getIntParam("id");
        String consulta = getParam("consulta");
        
        if (consulta == null || consulta.trim().isEmpty()) {
            respuesta.put("Error", "Lo siento, la consulta no puede estar vacía.");
            return respuesta;
        }
        
        Dataset dataset = datasetDAO.findById(id);
        String collectionName = dataset.getCollectionName();
        ModeloEmbedding modeloEmbedding = dataset.getModeloEmbedding();
        RetrievalMethod retrievalMethod = dataset.getRetrievalMethod();
        
        List<Double> vectoresConsulta = embeddingService.obtenerVector(modeloEmbedding, consulta);
        
        String resultados = "";
        String nombreMetodo = retrievalMethod.getNombre() != null ? retrievalMethod.getNombre().toLowerCase() : "";
        
        // Evaluación pragmática del método
        boolean esHibrido = nombreMetodo.contains("hybrid") || nombreMetodo.contains("hibrid") || nombreMetodo.contains("híbrid");
        
        if (esHibrido) {
            EmbeddingService.VectorDisperso disperso = embeddingService.obtenerVectorDisperso(consulta);
            // Cambié el salto de línea simple por un doble salto para separar mejor los metadatos de los chunks
            resultados = String.join("\n\n", qdrantService.buscarHibrido(
                collectionName, 
                vectoresConsulta, 
                disperso.getIndices(), 
                disperso.getValores(), 
                dataset.getTopK() 
            ));
        } else {
            // Aplica Dense Retrieval para coincidencias exactas o como fallback
            resultados = String.join("\n\n", qdrantService.buscar(collectionName, vectoresConsulta, dataset.getTopK()));
        }
        
        if (resultados.isEmpty()) {
            respuesta.put("respuesta", "Lo siento, no se encontró información relevante para tu consulta.");
            return respuesta;
        }

        // Se eliminó la limpieza de regex. Los metadatos de Qdrant pasan directamente al LLM.
        String promptBase = (dataset.getSystemPrompt() != null && !dataset.getSystemPrompt().trim().isEmpty()) 
                            ? dataset.getSystemPrompt() 
                            : "Eres un asistente de la BCN. Responde basándote estrictamente en el contexto proporcionado.";

        String promptFinal = promptBase + "\n\n" +
                "--- CONTEXTO RECUPERADO ---\n" +
                resultados + "\n\n" +
                "--- PREGUNTA DEL USUARIO ---\n" +
                consulta + "\n\n" +
                "RESPUESTA:";
        
        String respuestaGenerada = llmService.generarRespuesta(promptFinal, dataset.getModeloLlm());
        respuesta.put("contexto", resultados); 
        respuesta.put("respuesta_generada", respuestaGenerada);

        return respuesta;
    }

    @Override
    protected void validateParams() throws Exception {
        validateRule(validator.validateInt("id"));
        validateRule(validator.validateNotEmpty("consulta"));
    }
}
package cl.bcn.commands;
import cl.bcn.model.Dataset;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DatasetDAO;
/**
 * Actualiza los parámetros RAG de un dataset por id, incluyendo chunk, overlap, top_k, prompt, modelos y métodos.
 */
public class ActualizarDatasetCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {


	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateNotEmpty("system_prompt"));
	}
	
	@Override
	protected JSONObject execute() throws Exception {
		DatasetDAO datasetDAO = getDAOFactory().getDatasetDAO();
		
		Dataset dataset = 	datasetDAO.findById(getIntParam("id"));
		
		if (dataset == null) {
			throw new Exception("Dataset no encontrado con id: " + getIntParam("id"));
		}
		
		dataset.setId(getIntParam("id"));
		dataset.setNombre(getParam("nombre"));
		dataset.setTamanoChunk(getIntParam("tamano_chunk"));
		dataset.setCollectionName(getParam("collection_name"));
		dataset.setOverlap(getIntParam("overlap"));
		dataset.setTopK(getIntParam("top_k"));
		dataset.setSystemPrompt(getParam("system_prompt"));
		dataset.setApi(getDAOFactory().getApiDAO().findById(getIntParam("id_api")));
		dataset.setModeloEmbedding(getDAOFactory().getModeloEmbeddingDAO().findById(getIntParam("id_modelo_embedding")));
		dataset.setModeloLlm(getDAOFactory().getModeloLlmDAO().findById(getIntParam("id_modelo_llm")));
		dataset.setRetrievalMethod(getDAOFactory().getRetrievalMethodDAO().findById(getIntParam("id_retrieval_method")));
		dataset.setIndexMethod(getDAOFactory().getIndexMethodDAO().findById(getIntParam("id_index_method")));
		datasetDAO.update(dataset);
		
		return new JSONObject().put("status", "success");
	}

}

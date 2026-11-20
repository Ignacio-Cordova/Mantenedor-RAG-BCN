package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import java.util.Date;
import cl.bcn.model.Dataset;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DatasetDAO;
import cl.bcn.util.QdrantNameGenerator;

/**
 * Crea un dataset con sus modelos, métodos RAG y parámetros de chunking; genera y persiste su colección Qdrant.
 */
public class AgregarDatasetCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	
	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("nombre"));
		validateRule(validator.validateNotEmpty("system_prompt"));
		validateRule(validator.validateInt("tamano_chunk"));
		validateRule(validator.validateInt("overlap"));
		validateRule(validator.validateInt("top_k"));
		validateRule(validator.validateInt("id_api"));
		validateRule(validator.validateInt("id_modelo_llm"));
		validateRule(validator.validateInt("id_modelo_embedding"));
		validateRule(validator.validateInt("id_retrieval_method"));
		validateRule(validator.validateInt("id_index_method"));
	}
	
	
	@Override
	protected JSONObject execute() throws Exception {
		String nombre = getParam("nombre");
		Integer tamano_chunk = getIntParam("tamano_chunk");
		Integer overlap = getIntParam("overlap");
		Integer top_k = getIntParam("top_k");
		String system_prompt = getParam("system_prompt");
		Integer id_api = getIntParam("id_api");
		Integer id_modelo_llm = getIntParam("id_modelo_llm");
		Integer id_modelo_embedding = getIntParam("id_modelo_embedding");
		Integer id_retrieval_method = getIntParam("id_retrieval_method");
		Integer id_index_method = getIntParam("id_index_method");		
		
		Dataset dataset = new Dataset();
		dataset.setNombre(nombre);
		dataset.setTamanoChunk(tamano_chunk);
		dataset.setOverlap(overlap);
		dataset.setTopK(top_k);
		dataset.setCollectionName(nombre);
		dataset.setSystemPrompt(system_prompt);
		dataset.setApi(getDAOFactory().getApiDAO().findById(id_api));
		dataset.setModeloLlm(getDAOFactory().getModeloLlmDAO().findById(id_modelo_llm));
		dataset.setModeloEmbedding(getDAOFactory().getModeloEmbeddingDAO().findById(id_modelo_embedding));
		dataset.setRetrievalMethod(getDAOFactory().getRetrievalMethodDAO().findById(id_retrieval_method));
		dataset.setIndexMethod(getDAOFactory().getIndexMethodDAO().findById(id_index_method));
		dataset.setCrdate(new Date());
		
		DatasetDAO dao = getDAOFactory().getDatasetDAO();
		Integer nuevoId = dao.insert(dataset);
		
		String nombreQdrant = QdrantNameGenerator.generarNombreColeccion(nuevoId);
		dataset.setId(nuevoId);
		dataset.setCollectionName(nombreQdrant);
		dao.update(dataset);
		JSONObject response = new JSONObject();	
		response.put("success", true);
		response.put("id_dataset", nuevoId);
		
		return response;
	}

}

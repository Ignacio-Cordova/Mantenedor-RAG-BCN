package cl.bcn.model;
import cl.bcn.model.ModeloLlm;
import cl.bcn.model.IndexMethod;
import java.util.Date;
import cl.bcn.model.Dataset;
import cl.bcn.model.RetrievalMethod;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.model.Api;
import org.json.JSONObject;

import java.util.Objects;

public class Dataset {
	Integer id;
	String nombre;
	Integer tamanoChunk;
	Integer overlap;
	String collectionName;
	Integer topK;
	String systemPrompt;
	Api Api;
	ModeloLlm modeloLlm;
	ModeloEmbedding modeloEmbedding;
	RetrievalMethod retrievalMethod;
	IndexMethod indexMethod;
	Date crdate;
	public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("id", this.id);
        json.put("nombre", this.nombre);
        json.put("crdate", this.crdate);
        json.put("tamanoChunk", this.tamanoChunk);
        json.put("overlap", this.overlap);
        json.put("collectionName", this.collectionName);
        json.put("topK", this.topK); 
        json.put("systemPrompt", this.systemPrompt);
        
        if (this.Api != null) {
            json.put("idApi", this.Api.getId());
        } else {
            json.put("idApi", JSONObject.NULL);
        }
        if (this.modeloLlm != null) {
            json.put("idModeloLlm", this.modeloLlm.getId());
            json.put("nombreModeloLlm", this.modeloLlm.getNombre());
        } else {
            json.put("idModeloLlm", JSONObject.NULL);
        }
        if (this.modeloEmbedding != null) {
            json.put("idModeloEmbedding", this.modeloEmbedding.getId());
        } else {
            json.put("idModeloEmbedding", JSONObject.NULL);
        }
        if (this.retrievalMethod != null) {
            json.put("idRetrievalMethod", this.retrievalMethod.getId());
        } else {
            json.put("idRetrievalMethod", JSONObject.NULL);
        }

        if (this.indexMethod != null) {
            json.put("idIndexMethod", this.indexMethod.getId());
        } else {
            json.put("idIndexMethod", JSONObject.NULL);
        }

        return json;
    }
	

	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Integer getTamanoChunk() {
		return tamanoChunk;
	}
	public void setTamanoChunk(Integer tamanoChunk) {
		this.tamanoChunk = tamanoChunk;
	}
	public Integer getOverlap() {
		return overlap;
	}
	public void setOverlap(Integer overlap) {
		this.overlap = overlap;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public Integer getTopK() {
		return topK;
	}
	public void setTopK(Integer topK) {
		this.topK = topK;
	}
	public String getSystemPrompt() {
		return systemPrompt;
	}
	public void setSystemPrompt(String systemPrompt) {
		this.systemPrompt = systemPrompt;
	}
	public Api getApi() {
		return Api;
	}
	public void setApi(Api api) {
		Api = api;
	}
	public ModeloLlm getModeloLlm() {
		return modeloLlm;
	}
	public void setModeloLlm(ModeloLlm modeloLlm) {
		this.modeloLlm = modeloLlm;
	}
	public ModeloEmbedding getModeloEmbedding() {
		return modeloEmbedding;
	}
	public void setModeloEmbedding(ModeloEmbedding modeloEmbedding) {
		this.modeloEmbedding = modeloEmbedding;
	}
	public RetrievalMethod getRetrievalMethod() {
		return retrievalMethod;
	}
	public void setRetrievalMethod(RetrievalMethod retrievalMethod) {
		this.retrievalMethod = retrievalMethod;
	}
	public IndexMethod getIndexMethod() {
		return indexMethod;
	}
	public void setIndexMethod(IndexMethod indexMethod) {
		this.indexMethod = indexMethod;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(Api, collectionName, crdate, id, indexMethod, modeloEmbedding, modeloLlm, nombre, overlap,
				retrievalMethod, tamanoChunk, topK);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dataset other = (Dataset) obj;
		return Objects.equals(Api, other.Api) && Objects.equals(collectionName, other.collectionName)
				&& Objects.equals(crdate, other.crdate) && Objects.equals(id, other.id)
				&& Objects.equals(indexMethod, other.indexMethod)
				&& Objects.equals(modeloEmbedding, other.modeloEmbedding) && Objects.equals(modeloLlm, other.modeloLlm)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(overlap, other.overlap)
				&& Objects.equals(retrievalMethod, other.retrievalMethod)
				&& Objects.equals(tamanoChunk, other.tamanoChunk) && Objects.equals(topK, other.topK);
	}
	
	
	
}

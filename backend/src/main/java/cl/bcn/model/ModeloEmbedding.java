package cl.bcn.model;
import java.util.Date;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.model.Api;
import org.json.JSONObject;

import java.util.Objects;

public class ModeloEmbedding {
	Integer id;
	String nombreTecnico;
	Integer dimensiones;
	Integer maxTokensInput;
	Api api;
	Date crdate;
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    
	    json.put("id", this.id);
	    json.put("nombreTecnico", this.nombreTecnico);
	    json.put("dimensiones", this.dimensiones);
	    json.put("proveedor", this.api != null ? this.api.getNombreProveedor() : JSONObject.NULL);
	    json.put("idApi", this.api != null ? this.api.getId() : JSONObject.NULL);
	    json.put("maxTokensInput", this.maxTokensInput);
	    json.put("crdate", this.crdate);

	    return json;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombreTecnico() {
		return nombreTecnico;
	}
	public void setNombreTecnico(String nombre_tecnico) {
		this.nombreTecnico = nombre_tecnico;
	}
	public Integer getDimensiones() {
		return dimensiones;
	}
	public void setDimensiones(Integer dimensiones) {
		this.dimensiones = dimensiones;
	}
	public Api getApi() {
		return api;
	}
	public void setApi(Api api) {
		this.api = api;
	}
	public Integer getMaxTokensInput() {
		return maxTokensInput;
	}
	public void setMaxTokensInput(Integer max_tokens_input) {
		this.maxTokensInput = max_tokens_input;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(crdate, dimensiones, id, maxTokensInput, nombreTecnico, api);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModeloEmbedding other = (ModeloEmbedding) obj;
		return Objects.equals(crdate, other.crdate) && Objects.equals(dimensiones, other.dimensiones)
				&& Objects.equals(id, other.id) && Objects.equals(maxTokensInput, other.maxTokensInput)
				&& Objects.equals(nombreTecnico, other.nombreTecnico) && Objects.equals(api, other.api);
	}
	
}

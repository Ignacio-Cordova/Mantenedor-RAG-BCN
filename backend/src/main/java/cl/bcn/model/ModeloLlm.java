package cl.bcn.model;
import cl.bcn.model.ModeloLlm;
import java.util.Date;
import cl.bcn.model.Api;
import org.json.JSONObject;

import java.util.Objects;

public class ModeloLlm {
	Integer id;
	String nombre;
	String nombreTecnico;
	Integer limiteTokens;
	Api api;
	Date crdate;
	String endpoint;
	String plantilla_payload;
	String ruta_respuesta;
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();

	    json.put("id", this.id);
	    json.put("nombre", this.nombre);
	    json.put("nombreTecnico", this.nombreTecnico);
	    json.put("limiteTokens", this.limiteTokens);
	    json.put("endpoint", this.endpoint == null ? JSONObject.NULL : this.endpoint);
	    json.put("plantilla_payload", this.plantilla_payload == null ? JSONObject.NULL : this.plantilla_payload);
	    json.put("ruta_respuesta", this.ruta_respuesta == null ? JSONObject.NULL : this.ruta_respuesta);
	    if (this.api != null) {
	        json.put("idApi", this.api.getId());
	        json.put("nombreProveedor", this.api.getNombreProveedor());
	    } else {
	        json.put("idApi", JSONObject.NULL);
	    }

	    json.put("crdate", this.crdate);

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

	public String getNombreTecnico() {
		return nombreTecnico;
	}

	public void setNombreTecnico(String nombreTecnico) {
		this.nombreTecnico = nombreTecnico;
	}

	public Integer getLimiteTokens() {
		return limiteTokens;
	}

	public void setLimiteTokens(Integer limiteTokens) {
		this.limiteTokens = limiteTokens;
	}

	public Api getApi() {
		return api;
	}

	public void setApi(Api api) {
		this.api = api;
	}

	public Date getCrdate() {
		return crdate;
	}

	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getPlantillaPayload() {
		return plantilla_payload;
	}

	public void setPlantillaPayload(String plantilla_payload) {
		this.plantilla_payload = plantilla_payload;
	}

	public String getRutaRespuesta() {
		return ruta_respuesta;
	}

	public void setRutaRespuesta(String ruta_respuesta) {
		this.ruta_respuesta = ruta_respuesta;
	}

	@Override
	public int hashCode() {
		return Objects.hash(api, crdate, endpoint, id, limiteTokens, nombre, nombreTecnico, plantilla_payload,
				ruta_respuesta);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModeloLlm other = (ModeloLlm) obj;
		return Objects.equals(api, other.api) && Objects.equals(crdate, other.crdate)
				&& Objects.equals(endpoint, other.endpoint) && Objects.equals(id, other.id)
				&& Objects.equals(limiteTokens, other.limiteTokens) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(nombreTecnico, other.nombreTecnico)
				&& Objects.equals(plantilla_payload, other.plantilla_payload)
				&& Objects.equals(ruta_respuesta, other.ruta_respuesta);
	}

}
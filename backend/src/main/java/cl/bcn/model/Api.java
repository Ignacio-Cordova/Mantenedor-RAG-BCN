package cl.bcn.model;
import java.util.Date;
import org.json.JSONObject;

import java.util.Objects;

public class Api {
	Integer id;
	String nombreProveedor;
	String baseUrl;
	String organizacionId;
	String tipoAutenticacion;
	String apiKeyEncriptada;
	Date crdate;
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();

	    json.put("id", this.id);
	    json.put("nombreProveedor", this.nombreProveedor);
	    json.put("baseUrl", this.baseUrl);
	    json.put("organizacionId", this.organizacionId);
	    json.put("tipoAutenticacion", this.tipoAutenticacion);
	    json.put("apiKeyEncriptada", this.apiKeyEncriptada);
	    json.put("crdate", this.crdate);

	    return json;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombreProveedor() {
		return nombreProveedor;
	}
	public void setNombreProveedor(String nombre_proveedor) {
		this.nombreProveedor = nombre_proveedor;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String base_url) {
		this.baseUrl = base_url;
	}
	public String getOrganizacionId() {
		return organizacionId;
	}
	public void setOrganizacionId(String organizacion_id) {
		this.organizacionId = organizacion_id;
	}
	public String getTipoAutenticacion() {
		return tipoAutenticacion;
	}
	public void setTipoAutenticacion(String tipo_autenticacion) {
		this.tipoAutenticacion = tipo_autenticacion;
	}
	public String getApiKeyEncriptada() {
		return apiKeyEncriptada;
	}
	public void setApiKeyEncriptada(String api_key_encriptada) {
		this.apiKeyEncriptada = api_key_encriptada;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(apiKeyEncriptada, baseUrl, crdate, id, nombreProveedor, organizacionId,
				tipoAutenticacion);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Api other = (Api) obj;
		return Objects.equals(apiKeyEncriptada, other.apiKeyEncriptada) && Objects.equals(baseUrl, other.baseUrl)
				&& Objects.equals(crdate, other.crdate) && Objects.equals(id, other.id)
				&& Objects.equals(nombreProveedor, other.nombreProveedor)
				&& Objects.equals(organizacionId, other.organizacionId)
				&& Objects.equals(tipoAutenticacion, other.tipoAutenticacion);
	}

}

package cl.bcn.model;
import cl.bcn.model.Fuente;
import cl.bcn.model.ServicioWeb;
import org.json.JSONObject;

import java.util.Objects;

public class ServicioWeb extends Fuente{
	Integer id;
	String urlEndpoint;
	String metodoHttp;
	String headersJson;
	String bodyPayload;
	String rutaTexto;
	String rutasMetadatos;
		public JSONObject toJSON() {
		    JSONObject json = super.toJSON();

		    json.put("id", this.id);
		    json.put("urlEndpoint", this.urlEndpoint);
		    json.put("metodoHttp", this.metodoHttp);
		    json.put("headersJson", this.headersJson);
		    json.put("bodyPayload", this.bodyPayload);
		    json.put("rutaTexto", this.rutaTexto);
		    json.put("rutasMetadatos", this.rutasMetadatos);
		    
		    return json;
		}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUrlEndpoint() {
		return urlEndpoint;
	}
	public void setUrlEndpoint(String urlEndpoint) {
		this.urlEndpoint = urlEndpoint;
	}
	public String getMetodoHttp() {
		return metodoHttp;
	}
	public void setMetodoHttp(String metodoHttp) {
		this.metodoHttp = metodoHttp;
	}
	public String getHeadersJson() {
		return headersJson;
	}
	public void setHeadersJson(String headersJson) {
		this.headersJson = headersJson;
	}
	public String getBodyPayload() {
		return bodyPayload;
	}
	public void setBodyPayload(String bodyPayload) {
		this.bodyPayload = bodyPayload;
	}
	public String getRutaTexto() {
		return rutaTexto;
	}
	public void setRutaTexto(String rutaTexto) {
		this.rutaTexto = rutaTexto;
	}
	public String getRutasMetadatos() {
		return rutasMetadatos;
	}
	public void setRutasMetadatos(String rutasMetadatos) {
		this.rutasMetadatos = rutasMetadatos;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(bodyPayload, headersJson, id, metodoHttp, rutaTexto, urlEndpoint, rutasMetadatos);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServicioWeb other = (ServicioWeb) obj;
		return Objects.equals(bodyPayload, other.bodyPayload) 
				&& Objects.equals(headersJson, other.headersJson)
				&& Objects.equals(id, other.id) 
				&& Objects.equals(metodoHttp, other.metodoHttp)
				&& Objects.equals(rutaTexto, other.rutaTexto) 
				&& Objects.equals(urlEndpoint, other.urlEndpoint)
				&& Objects.equals(rutasMetadatos, other.rutasMetadatos); 
	}
	
}

package cl.bcn.model;
import java.util.Date;
import cl.bcn.model.ServidorVectorial;
import org.json.JSONObject;

import java.util.Objects;

public class ServidorVectorial {
	Integer id;
	String proveedor;
	String hostUrl;
	Integer puerto;
	String apiKey;
	Date crdate;
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    
	    json.put("id", this.id);
	    json.put("proveedor", this.proveedor);
	    json.put("hostUrl", this.hostUrl);
	    json.put("puerto", this.puerto);
	    json.put("apiKey", this.apiKey);
	    json.put("crdate", this.crdate);

	    return json;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	public String getHostUrl() {
		return hostUrl;
	}
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	public Integer getPuerto() {
		return puerto;
	}
	public void setPuerto(Integer puerto) {
		this.puerto = puerto;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	public ServidorVectorial() {
		super();
	}
	@Override
	public int hashCode() {
		return Objects.hash(apiKey, crdate, hostUrl, id, proveedor, puerto);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServidorVectorial other = (ServidorVectorial) obj;
		return Objects.equals(apiKey, other.apiKey) && Objects.equals(crdate, other.crdate)
				&& Objects.equals(hostUrl, other.hostUrl) && Objects.equals(id, other.id)
				&& Objects.equals(proveedor, other.proveedor) && Objects.equals(puerto, other.puerto);
	}
	
	
	
}

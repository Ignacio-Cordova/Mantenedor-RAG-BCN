package cl.bcn.model;
import cl.bcn.model.Fuente;
import java.util.Date;
import cl.bcn.model.Dataset;
import org.json.JSONObject;

import java.util.Objects;

public class Fuente {
	Integer id;
	String nombre;
	String tipo;
	String estadoIndexacion;
	String logError;
	String idExternoVectorial;
	String idIndexacion;
	Dataset dataset;
	Date crdate;
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    
	    json.put("id", this.id);
	    json.put("nombre", this.nombre);
	    json.put("tipo", this.tipo);
	    json.put("estadoIndexacion", this.estadoIndexacion);
	    json.put("logError", this.logError);
	    json.put("idExternoVectorial", this.idExternoVectorial);
	    json.put("idIndexacion", this.idIndexacion);
	    if (this.dataset != null) {
	        json.put("idDataset", this.dataset.getId());
	        json.put("nombreDataset", this.dataset.getNombre());
	    } else {
	        json.put("idDataset", JSONObject.NULL);
	        json.put("nombreDataset", JSONObject.NULL);
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
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getEstadoIndexacion() {
		return estadoIndexacion;
	}
	public void setEstadoIndexacion(String estado_indexacion) {
		this.estadoIndexacion = estado_indexacion;
	}
	public String getLogError() {
		return logError;
	}
	public void setLogError(String log_error) {
		this.logError = log_error;
	}
	public String getIdExternoVectorial() {
		return idExternoVectorial;
	}
	public void setIdExternoVectorial(String id_externo_vectorial) {
		this.idExternoVectorial = id_externo_vectorial;
	}
	public String getIdIndexacion() {
		return idIndexacion;
	}
	public void setIdIndexacion(String id_indexacion) {
		this.idIndexacion = id_indexacion;
	}
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(crdate, dataset, estadoIndexacion, id, idExternoVectorial, idIndexacion, logError,
				nombre, tipo);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fuente other = (Fuente) obj;
		return Objects.equals(crdate, other.crdate) && Objects.equals(dataset, other.dataset)
				&& Objects.equals(estadoIndexacion, other.estadoIndexacion) && Objects.equals(id, other.id)
				&& Objects.equals(idExternoVectorial, other.idExternoVectorial)
				&& Objects.equals(idIndexacion, other.idIndexacion) && Objects.equals(logError, other.logError)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(tipo, other.tipo);
	}
	
}
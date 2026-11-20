package cl.bcn.model;
import cl.bcn.model.IndexMethod;
import java.util.Date;
import org.json.JSONObject;

import java.util.Objects;

public class IndexMethod {
	
	Integer id;
	String nombre;
	String descripcion;
	Date crdate;
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();

	    json.put("id", this.id);
	    json.put("nombre", this.nombre);
	    json.put("descripcion", this.descripcion);
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
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(crdate, descripcion, id, nombre);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexMethod other = (IndexMethod) obj;
		return Objects.equals(crdate, other.crdate) && Objects.equals(descripcion, other.descripcion)
				&& Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre);
	}

}

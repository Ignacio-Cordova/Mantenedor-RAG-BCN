package cl.bcn.model;
import java.util.Date;
import cl.bcn.model.Conexion;
import org.json.JSONObject;

import java.util.Objects;

public class Conexion {
	Integer id;
	String nombre;
	Integer puerto;
	String password;
	String usuario;
	String host;
	String motor;
	Date crdate;
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();

	    json.put("id", this.id);
	    json.put("nombre", this.nombre);
	    json.put("puerto", this.puerto);
	    json.put("password", this.password);
	    json.put("usuario", this.usuario);
	    json.put("host", this.host);
	    json.put("motor", this.motor);
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
	public Integer getPuerto() {
		return puerto;
	}
	public void setPuerto(Integer puerto) {
		this.puerto = puerto;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getMotor() {
		return motor;
	}
	public void setMotor(String motor) {
		this.motor = motor;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(crdate, host, id, motor, nombre, password, puerto, usuario);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conexion other = (Conexion) obj;
		return Objects.equals(crdate, other.crdate) && Objects.equals(host, other.host) && Objects.equals(id, other.id)
				&& Objects.equals(motor, other.motor) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(password, other.password) && Objects.equals(puerto, other.puerto)
				&& Objects.equals(usuario, other.usuario);
	}
}
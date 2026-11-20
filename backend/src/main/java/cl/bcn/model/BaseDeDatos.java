package cl.bcn.model;
import cl.bcn.model.BaseDeDatos;
import cl.bcn.model.Fuente;
import cl.bcn.model.Conexion;
import org.json.JSONObject;

import java.util.Objects;

public class BaseDeDatos extends Fuente {
	
	String tablaObjetivo;
	String querySql;
	String modoExtraccion;
	String columnaContenido;
	Conexion conexion;
	public JSONObject toJSON() {
	    JSONObject json = super.toJSON();

	    json.put("tablaObjetivo", this.tablaObjetivo);
	    json.put("querySql", this.querySql);
	    json.put("modoExtraccion", this.modoExtraccion);
	    json.put("columnaContenido", this.columnaContenido);
	    if (this.conexion != null) {
	        json.put("idConexion", this.conexion.getId());     
	        json.put("nombreConexion", this.conexion.getNombre());
	    } else {
	        json.put("idConexion", JSONObject.NULL);
	    }

	    return json;
	}
	public String getTablaObjetivo() {
		return tablaObjetivo;
	}
	public void setTablaObjetivo(String tabla_objetivo) {
		this.tablaObjetivo = tabla_objetivo;
	}
	public String getQuerySql() {
		return querySql;
	}
	public void setQuerySql(String query_sql) {
		this.querySql = query_sql;
	}
	public String getModoExtraccion() {
		return modoExtraccion;
	}
	public void setModoExtraccion(String modo_extraccion) {
		this.modoExtraccion = modo_extraccion;
	}
	public String getColumnaContenido() {
		return columnaContenido;
	}
	public void setColumnaContenido(String columna_contenido) {
		this.columnaContenido = columna_contenido;
	}
	public Conexion getConexion() {
		return conexion;
	}
	public void setConexion(Conexion conexion) {
		this.conexion = conexion;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(columnaContenido, conexion, modoExtraccion, querySql, tablaObjetivo);
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
		BaseDeDatos other = (BaseDeDatos) obj;
		return Objects.equals(columnaContenido, other.columnaContenido)
				&& Objects.equals(conexion, other.conexion)
				&& Objects.equals(modoExtraccion, other.modoExtraccion) && Objects.equals(querySql, other.querySql)
				&& Objects.equals(tablaObjetivo, other.tablaObjetivo);
	}
}
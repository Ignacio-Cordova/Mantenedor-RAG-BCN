package cl.bcn.commands.autopagination;
/**
 * Describe un filtro opcional aplicado por los comandos de paginación.
 * Vincula el nombre recibido como parámetro con su consulta SQL y su tipo de conversión.
 */

public class FilterParameter {

	String paramName;
	String sql;
	Class type;
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((paramName == null) ? 0 : paramName.hashCode());
		result = prime * result + ((sql == null) ? 0 : sql.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterParameter other = (FilterParameter) obj;
		if (paramName == null) {
			if (other.paramName != null)
				return false;
		} else if (!paramName.equals(other.paramName))
			return false;
		if (sql == null) {
			if (other.sql != null)
				return false;
		} else if (!sql.equals(other.sql))
			return false;
		return true;
	}
	


    public Class getType() {
        return type;
    }
    
    public void setType(Class type) {
    	this.type = type;
    }
}

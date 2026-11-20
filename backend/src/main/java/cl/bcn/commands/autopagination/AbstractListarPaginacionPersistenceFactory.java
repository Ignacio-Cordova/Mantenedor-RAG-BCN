package cl.bcn.commands.autopagination;
import java.util.Date;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.Timestamp;
import cacatua.model.Paginacion;
public abstract class AbstractListarPaginacionPersistenceFactory<TIPO> extends AbstractListarPaginacion<TIPO> {

	GenericDAOJdbc df;
	protected abstract GenericDAOJdbc getDAO();

	protected GenericDAOJdbc getPersistenceDAO() {

		if(df == null) {
			df = getDAO();
		}
		return df;
	}
	
	@Override
	public Paginacion<TIPO> getPaginacion(String limit, String page, String patron) throws Exception {
		return getPersistenceDAO().getPaginacion(limit, page, patron);
	}
	
	@Override
	protected void initializeOptionalFilterParams() {
		if(optionalParams!=null) {
			for(FilterParameter p: optionalParams) {
				if(getParam(p.getParamName())!=null) {
					if(p.getType().equals(String.class)) {
						getPersistenceDAO().addOptionalStringFilterQueryParam(p.getParamName(), getParam(p.getParamName()), p.getSql());	
					} else if(p.getType().equals(Integer.class)) {
						getPersistenceDAO().addOptionalIntFilterQueryParam(p.getParamName(), getIntParam(p.getParamName()), p.getSql());	
					} else if(p.getType().equals(Long.class)) {
						getPersistenceDAO().addOptionalLongFilterQueryParam(p.getParamName(), getLongParam(p.getParamName()), p.getSql());	
					} else if(p.getType().equals(Date.class)) {
						getPersistenceDAO().addOptionalDateFilterQueryParam(p.getParamName(), getDateParam(p.getParamName()), p.getSql());	
					}else if(p.getType().equals(Timestamp.class)) {
						getPersistenceDAO().addOptionalTimestampFilterQueryParam(p.getParamName(), getDateParam(p.getParamName()), p.getSql());	
					} else if(p.getType().equals(Boolean.class)) {
						getPersistenceDAO().addOptionalBooleanFilterQueryParam(p.getParamName(), getBooleanParam(p.getParamName()), p.getSql());	
					}
					
				}
			}
		}
	}
	
}

package cl.bcn.commands.autopagination;
import cacatua.business.method.HTTPGetService;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import cacatua.business.commands.AbstractCommand;
import cacatua.model.Paginacion;
import cl.bcn.persistence.PersistenceFactory;

public abstract class AbstractListarPaginacion<TIPO> extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	protected String limit;
	protected String page;
	protected String patron;
	
	@Override
	protected JSONObject execute() throws Exception {
		
		limit = getParam("limit");
		page = getParam("page");
		patron = getParam("patron");
		
		initializeOptionalFilterParams();
		JSONObject out = getErrorDefaultResult();

		Paginacion<TIPO> paginacion =  null;

		paginacion =  getPaginacion(limit, page, patron);


		if(paginacion!=null) {
			out = paginacion.toJSONObject();
		}
		return out;

	}

	
	public JSONObject getTableHeader() throws IllegalArgumentException, IllegalAccessException {
		JSONObject out = new JSONObject();
		TIPO myclass = null;
        Field[] fs = myclass.getClass().getFields();
        for(Field f : fs) {
            f.setAccessible(true);
            Object value = f.get(myclass);
            	out.put( f.getName(),  f.getName());
        }
        return out;
     }


	public abstract Paginacion<TIPO> getPaginacion(String limit, String page, String patron)  throws Exception;
	


	



	@Override
	protected void validateParams() throws Exception {
		setOptionalFilterParams();
		validateRule(validator.validateIntIfExists("limit"));
		validateRule(validator.validateIntIfExists("page"));
		Set<String> parameters = new HashSet<String>();
		parameters.add("limit");
		parameters.add("page");
		parameters.add("patron");
		for(String s: getOptionalFilterParams()) {
			parameters.add(s);
		}
		String[] array = new String[parameters.size()];

		int i = 0;
		for (String s: parameters) {
			array[i++] = s;
		}
		validateRule(validator.validateMember(array));
	}

	protected void addOptionalFilterParam(String paramName, Class type, String sqlFilter) {
		if(optionalParams==null) {
			optionalParams = new LinkedHashSet<FilterParameter>();
		}
		FilterParameter param = new FilterParameter();
		param.setParamName(paramName);
		param.setSql(sqlFilter);
		param.setType(type);
		optionalParams.add(param);
	}
	
	Set<FilterParameter> optionalParams;
	

	protected abstract void initializeOptionalFilterParams();
	
	

	protected String[] getOptionalFilterParams() {
		if(optionalParams!=null) {
			String a[] =  new String[optionalParams.size()];
			int i = 0;
			for(FilterParameter p: optionalParams) {
				a[i++] = p.getParamName();
			}
			return a;
		}
		return new String[0];	
	}


	protected void setOptionalFilterParams() {
		
	}

}

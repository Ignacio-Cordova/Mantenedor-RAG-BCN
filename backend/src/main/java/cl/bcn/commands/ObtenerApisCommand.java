package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;

import java.util.List;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Api;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Lista todos los proveedores de API mediante el modelo Api.toJSON().
 */
public class ObtenerApisCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
	}

	@Override
	protected JSONArray execute() throws Exception {
		List<Api> listaApis = getDAOFactory().getApiDAO().findAll();
		return new JSONArray(listaApis);
	}
}

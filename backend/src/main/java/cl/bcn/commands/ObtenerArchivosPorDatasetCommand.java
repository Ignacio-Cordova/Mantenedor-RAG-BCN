package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.persistence.dao.ArchivoDAO;
import cl.bcn.model.Archivo;

import java.util.List;

import org.json.JSONArray;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
/**
 * Lista los archivos asociados a un dataset usando su identificador.
 */
public class ObtenerArchivosPorDatasetCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONArray execute() throws Exception {
		ArchivoDAO dao = getDAOFactory().getArchivoDAO();
		List<Archivo> archivos = dao.findAllByDataset(getIntParam("id"));
		return new JSONArray(archivos);
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));		
	}
	
}
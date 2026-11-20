package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.ServidorVectorial;

import java.util.List;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ServidorVectorialDAO;
/**
 * Lista los servidores vectoriales registrados mediante ServidorVectorial.toJSON().
 */
public class ObtenerServidoresVectorialesCommand extends AbstractCommand<JSONArray, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
	}

	@Override
	protected JSONArray execute() throws Exception {
		ServidorVectorialDAO dao = getDAOFactory().getServidorVectorialDAO();
		List<ServidorVectorial> servidores = dao.findAll();
		return new JSONArray(servidores);
	}
}
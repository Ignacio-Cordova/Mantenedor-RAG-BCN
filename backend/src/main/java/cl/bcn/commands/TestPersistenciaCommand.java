package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.Partido;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.PartidoDAO;
/**
 * Busca por id un registro Partido para verificar que la persistencia JDBC está operativa.
 */
public class TestPersistenciaCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected void validateParams() throws Exception {
	}

	@Override
	protected JSONObject execute() throws Exception {
		PartidoDAO partidoDAO = getDAOFactory().getPartidoDAO();
		Partido partido = partidoDAO.findById(getIntParam("id"));
		return new JSONObject(partido);
	}
}

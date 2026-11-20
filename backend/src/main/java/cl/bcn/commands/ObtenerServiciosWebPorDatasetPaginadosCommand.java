package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import org.json.JSONObject;

import java.util.List;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.ServicioWeb;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ServicioWebDAO;
/**
 * Lista fuentes REST de un dataset con paginación y cuenta el total asociado.
 */
public class ObtenerServiciosWebPorDatasetPaginadosCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {
	@Override
    protected JSONObject execute() throws Exception {
        ServicioWebDAO dao = getDAOFactory().getServicioWebDAO();
        List<ServicioWeb> serviciosWeb = dao.findAll(getIntParam("id"), getIntParam("limit"), getIntParam("offset"));
        int totalRegistros = dao.countByDataset(getIntParam("id"));
        JSONArray jsonArray = new JSONArray();
        for (ServicioWeb sw : serviciosWeb) {
            jsonArray.put(sw.toJSON());
        }
        JSONObject response = new JSONObject();
        response.put("data", jsonArray);
        response.put("total", totalRegistros);
        
        return response;
    }

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateNotEmpty("limit"));
		validateRule(validator.validateNotEmpty("offset"));		
		validateRule(validator.validateInt("id"));
		validateRule(validator.validateInt("limit"));
		validateRule(validator.validateInt("offset"));		
	}
}

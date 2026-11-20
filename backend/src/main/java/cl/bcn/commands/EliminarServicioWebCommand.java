package cl.bcn.commands;
import cl.bcn.model.Fuente;
import org.json.JSONObject;

import java.util.List;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Documento;
import cl.bcn.model.ServicioWeb;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DocumentoDAO;
import cl.bcn.persistence.dao.FuenteDAO;
import cl.bcn.persistence.dao.ServicioWebDAO;
import cl.bcn.util.QdrantService;
/**
 * Elimina los vectores y documentos de una fuente REST y después borra su configuración PostgreSQL.
 */
public class EliminarServicioWebCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		QdrantService qDrantService = new QdrantService();
		ServicioWebDAO servicioWebDAO = getDAOFactory().getServicioWebDAO();
		FuenteDAO fuenteDAO = getDAOFactory().getFuenteDAO();
		DocumentoDAO documentoDAO = getDAOFactory().getDocumentoDAO();
		
		Integer idFuente = getIntParam("id");
		
		ServicioWeb servicioWeb = servicioWebDAO.findById(idFuente);
		if (servicioWeb == null) {
			return new JSONObject().put("error", "El servicio web no existe o ya fue eliminado.");
		}
		
		String collectionName = servicioWeb.getDataset().getCollectionName();
		
		List<Documento> documentos = documentoDAO.findByIdFuente(idFuente); 
		
		if (documentos != null && !documentos.isEmpty()) {
			for (Documento doc : documentos) {
				qDrantService.eliminarPorIdDocumento(collectionName, doc.getId());
			}
			documentoDAO.deleteByIdFuente(idFuente);
		}
		
		servicioWebDAO.delete(servicioWeb); 
		fuenteDAO.delete(idFuente);
		
		return new JSONObject()
				.put("status", "success")
				.put("message", "Servicio Web y fuente eliminados correctamente.");
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}	
}

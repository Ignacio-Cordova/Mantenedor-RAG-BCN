package cl.bcn.commands;
import cl.bcn.model.BaseDeDatos;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.dao.BaseDeDatosDAO;
import org.json.JSONObject;

import java.util.List;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Documento;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DocumentoDAO;
import cl.bcn.persistence.dao.FuenteDAO;
import cl.bcn.util.QdrantService;
/**
 * Elimina los documentos y vectores de una fuente JDBC PostgreSQL y luego borra su configuración PostgreSQL.
 */
public class EliminarBaseDeDatosCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		QdrantService qDrantService = new QdrantService();
		BaseDeDatosDAO baseDeDatosDAO = getDAOFactory().getBaseDeDatosDAO();
		FuenteDAO fuenteDAO = getDAOFactory().getFuenteDAO();
		DocumentoDAO documentoDAO = getDAOFactory().getDocumentoDAO();
		
		Integer idFuente = getIntParam("id");
		BaseDeDatos baseDeDatos = baseDeDatosDAO.findById(idFuente);
		
		if (baseDeDatos == null) {
			return new JSONObject().put("error", "La base de datos no existe o ya fue eliminada.");
		}
		String collectionName = baseDeDatos.getDataset().getCollectionName();
		
		List<Documento> documentos = documentoDAO.findByIdFuente(getIntParam("id"));
		if (documentos != null && !documentos.isEmpty()) {
			for (Documento doc : documentos) {
				qDrantService.eliminarPorIdDocumento(collectionName, doc.getId());
			}
			documentoDAO.deleteByIdFuente(idFuente);
		}
		baseDeDatosDAO.delete(getIntParam("id"));
		fuenteDAO.delete(getIntParam("id"));
		
		return new JSONObject()
				.put("status", "success")
				.put("message", "Base de datos y fuente eliminados correctamente.");
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));			
	}


}

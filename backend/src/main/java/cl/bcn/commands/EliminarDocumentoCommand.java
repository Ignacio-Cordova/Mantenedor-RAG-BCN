package cl.bcn.commands;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Documento;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DocumentoDAO;
import cl.bcn.util.QdrantService;
/**
 * Elimina primero los puntos Qdrant asociados al documento_id y después el documento PostgreSQL correspondiente.
 */
public class EliminarDocumentoCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		DocumentoDAO documentoDAO = getDAOFactory().getDocumentoDAO();
		Documento documento = documentoDAO.findById(getIntParam("id"));
		if (documento == null) {
			return new JSONObject().put("error", "El documento no existe o ya fue eliminado.");
		}

		String collectionName = documento.getFuente().getDataset().getCollectionName();
		new QdrantService().eliminarPorIdDocumento(collectionName, documento.getId());
		Integer filasAfectadas = documentoDAO.delete(documento);
		return new JSONObject()
				.put("status", "success")
				.put("filasAfectadas", filasAfectadas);
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}

}

package cl.bcn.commands;
import org.json.JSONObject;

import java.util.List;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Documento;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DocumentoDAO;
import cl.bcn.persistence.dao.FuenteDAO;
import cl.bcn.util.QdrantService;
/**
 * Elimina por filtro los vectores de todos los documentos de una fuente y luego elimina documentos y fuente PostgreSQL.
 */
public class EliminarFuenteCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		FuenteDAO dao = getDAOFactory().getFuenteDAO();
		DocumentoDAO documentoDAO = getDAOFactory().getDocumentoDAO();
		Fuente fuente = dao.findById(getIntParam("id"));
		if (fuente == null) {
			return new JSONObject().put("error", "La fuente no existe o ya fue eliminada.");
		}

		List<Documento> documentos = documentoDAO.findByIdFuente(fuente.getId());
		QdrantService qdrantService = new QdrantService();
		for (Documento documento : documentos) {
			qdrantService.eliminarPorIdDocumento(
					fuente.getDataset().getCollectionName(), documento.getId());
		}
		if (!documentos.isEmpty()) {
			documentoDAO.deleteByIdFuente(fuente.getId());
		}

		Integer filasAfectadas = dao.delete(fuente.getId());
		return new JSONObject()
				.put("status", "success")
				.put("filasAfectadas", filasAfectadas);
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
	}


}

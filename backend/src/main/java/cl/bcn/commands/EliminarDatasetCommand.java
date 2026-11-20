package cl.bcn.commands;
import cl.bcn.persistence.dao.ArchivoDAO;
import cl.bcn.model.Archivo;
import cl.bcn.persistence.dao.BaseDeDatosDAO;
import cl.bcn.model.Dataset;
import org.json.JSONObject;

import java.util.List;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Documento;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DatasetDAO;
import cl.bcn.persistence.dao.DocumentoDAO;
import cl.bcn.persistence.dao.FuenteDAO;
import cl.bcn.util.QdrantService;
/**
 * Elimina la colección Qdrant y en cascada las fuentes, documentos y registro PostgreSQL del dataset.
 */
public class EliminarDatasetCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		QdrantService qDrantService = new QdrantService();
		DatasetDAO datasetDAO = getDAOFactory().getDatasetDAO();
		FuenteDAO fuenteDAO = getDAOFactory().getFuenteDAO();
		DocumentoDAO documentoDAO = getDAOFactory().getDocumentoDAO();
		ArchivoDAO archivoDAO = getDAOFactory().getArchivoDAO();
		BaseDeDatosDAO baseDeDatosDAO = getDAOFactory().getBaseDeDatosDAO();
		
		Integer idDataset = getIntParam("id");
		
		String collectionName = datasetDAO.findById(idDataset).getCollectionName();
		
		qDrantService.eliminarColeccion(collectionName);
		
		List<Fuente> fuentesAsociadas = fuenteDAO.findAllByDataset(getIntParam("id"));
		
		for (Fuente fuente : fuentesAsociadas) {
			Integer idFuente = fuente.getId();
			if ("archivo".equalsIgnoreCase(fuente.getTipo())) {
				Documento doc = documentoDAO.findByIdArchivo(idFuente);
				if (doc != null) {
					documentoDAO.deleteByIdFuente(idFuente);
				}
				archivoDAO.delete(idFuente);
			} else if ("base_de_datos".equalsIgnoreCase(fuente.getTipo())) {
				List<Documento> documentos = documentoDAO.findByIdFuente(idFuente);
				if (documentos != null && !documentos.isEmpty()) {
					for (Documento documento : documentos) {
						documentoDAO.delete(documento);
					}
				}
				baseDeDatosDAO.delete(idFuente);
			} else if ("servicio_web".equalsIgnoreCase(fuente.getTipo())) {
				List<Documento> documentos = documentoDAO.findByIdFuente(idFuente);

				if (documentos != null && !documentos.isEmpty()) {
					for (Documento documento : documentos) {
						documentoDAO.delete(documento);
					}
				}
			}
			fuenteDAO.delete(idFuente);
		}
		
		
		Integer dataset = datasetDAO.delete(getIntParam("id"));
		return new JSONObject()
				.put("status", "success")
				.put("datasetEliminado", idDataset)
				.put("filasAfectadas", dataset);
	}
		
	
	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));		
	}
}

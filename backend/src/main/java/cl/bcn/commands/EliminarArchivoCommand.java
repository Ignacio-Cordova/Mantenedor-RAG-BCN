package cl.bcn.commands;
import cl.bcn.persistence.dao.ArchivoDAO;
import cl.bcn.model.Archivo;
import org.json.JSONObject;

import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Documento;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DocumentoDAO;
import cl.bcn.persistence.dao.FuenteDAO;
import cl.bcn.util.QdrantService;
/**
 * Elimina vectores por documento, registros PostgreSQL y el archivo físico asociado a una fuente ARCHIVO.
 */
public class EliminarArchivoCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		QdrantService qDrantService = new QdrantService();
		ArchivoDAO archivoDAO = getDAOFactory().getArchivoDAO();
		FuenteDAO fuenteDAO = getDAOFactory().getFuenteDAO();
		DocumentoDAO documentoDAO = getDAOFactory().getDocumentoDAO();
		
		Integer idArchivo = getIntParam("id");
		Archivo archivo = archivoDAO.findById(idArchivo);
		if (archivo == null) {
			return new JSONObject().put("error", "El archivo no existe o ya fue eliminado.");
		}
		
		String rutaFisica = archivo.getRuta(); 
		String collectionName = archivo.getDataset().getCollectionName();
		List<Documento> documentosAsociados = documentoDAO.findByIdFuente(idArchivo);
		if (documentosAsociados != null && !documentosAsociados.isEmpty()) {
			for (Documento documento : documentosAsociados) {
				qDrantService.eliminarPorIdDocumento(collectionName, documento.getId());
			}
			documentoDAO.deleteByIdFuente(idArchivo);
		}
		archivoDAO.delete(idArchivo);
		fuenteDAO.delete(idArchivo);
		if (rutaFisica != null && !rutaFisica.trim().isEmpty()) {
			try {
				Path pathArchivo = Paths.get(rutaFisica);
				boolean borradoFisico = Files.deleteIfExists(pathArchivo); 
				
				if (borradoFisico) {
				} else {
				}
			} catch (Exception e) {
				System.err.println("No se pudo borrar el archivo físico: " + e.getMessage());
			}
		}
		return new JSONObject()
				.put("status", "success")
				.put("message", "Archivo eliminado correctamente de la BD, Qdrant y del disco.");
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}


}

package cl.bcn.commands;
import cl.bcn.model.IndexMethod;
import cl.bcn.model.BaseDeDatos;
import cl.bcn.model.Archivo;
import java.util.Date;
import cl.bcn.model.Dataset;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Documento;
import cl.bcn.model.Fuente;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.model.ServicioWeb;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.DatasetDAO;
import cl.bcn.persistence.dao.DocumentoDAO;
import cl.bcn.util.EmbeddingService;
import cl.bcn.util.ExtractorJDBC;
import cl.bcn.util.ExtractorServicioWeb;
import cl.bcn.util.QdrantService;
import cl.bcn.util.TextSplitter;
/**
 * Vacía y recrea la colección del dataset, eliminando puntos antiguos antes de reconstruir todas las fuentes.
 */
public class ReIndexarDatasetCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		DatasetDAO datasetDAO = getDAOFactory().getDatasetDAO();
		DocumentoDAO documentoDAO = getDAOFactory().getDocumentoDAO();
		Dataset dataset = datasetDAO.findById(getIntParam("id"));
		IndexMethod indexMethod = dataset.getIndexMethod();
		List<Fuente> fuentes = getDAOFactory().getFuenteDAO().findAllByDataset(dataset.getId());
		if (fuentes == null || fuentes.isEmpty()) {
			return new JSONObject().put("status", "No hay fuentes asociadas a este dataset");
		}
		Integer tamanoChunk = dataset.getTamanoChunk();
		String nombreColeccion = dataset.getCollectionName();
		ModeloEmbedding modeloEmbedding = dataset.getModeloEmbedding();				
		Integer overlap = dataset.getOverlap();
		if (overlap == null || overlap < 0) {
			overlap = (int) (tamanoChunk * 0.1);
		}
		
		EmbeddingService servicioEmbedding = new EmbeddingService();
		QdrantService qdrantService = new QdrantService();
		String metodoRecuperacion = (dataset.getRetrievalMethod() != null && dataset.getRetrievalMethod().getNombre() != null) 
		    ? dataset.getRetrievalMethod().getNombre().toLowerCase() : "";
		boolean esHibrida = metodoRecuperacion.contains("hibrida") || 
							metodoRecuperacion.contains("híbrida") || 
							metodoRecuperacion.contains("hybrid");
		qdrantService.eliminarColeccion(nombreColeccion);
		qdrantService.crearColeccionSiNoExiste(nombreColeccion, modeloEmbedding.getDimensiones(), esHibrida);
		boolean huboErrores = false;

		for (Fuente fuente : fuentes) {
			List<Documento> documentosExistentes = documentoDAO.findByIdFuente(fuente.getId());
			if (documentosExistentes != null && !documentosExistentes.isEmpty()) {
				for (Documento documento : documentosExistentes) {
					qdrantService.eliminarPorIdDocumento(nombreColeccion, documento.getId());
				}
				documentoDAO.deleteByIdFuente(fuente.getId());
			}
			
			Integer idFuente = fuente.getId();
			getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "INDEXANDO", null);
			try {
				if (fuente.getTipo().equals("ARCHIVO")) {
				Archivo archivo = getDAOFactory().getArchivoDAO().findById(fuente.getId());
				
				String textoExtraido = new String(Files.readAllBytes(Paths.get(archivo.getRuta())), StandardCharsets.UTF_8);
				Documento documento = guardarDocumentoBD(fuente, textoExtraido, null);
				
				List<String> chunks = fragmentarTexto(documento.getTexto(), indexMethod, tamanoChunk, overlap);
				procesarEIndexarChunks(chunks, dataset, documento, nombreColeccion, servicioEmbedding, qdrantService, null);
			}
			
			else if (fuente.getTipo().equals("BASE_DE_DATOS")) {
				BaseDeDatos BD = getDAOFactory().getBaseDeDatosDAO().findById(fuente.getId());
				List<String> textosReales = ExtractorJDBC.extraerTextos(BD);
				if (textosReales == null) {
					textosReales = java.util.Collections.emptyList(); 
				} else {
				}
				
				for (String textoOriginal : textosReales) {
					Documento documento = guardarDocumentoBD(fuente, textoOriginal, null);
					List<String> chunks = fragmentarTexto(documento.getTexto(), indexMethod, tamanoChunk, overlap);
					
					procesarEIndexarChunks(chunks, dataset, documento, nombreColeccion, servicioEmbedding, qdrantService, null);
				}
			}
			
			else if (fuente.getTipo().equals("SERVICIO_WEB")) {
				ServicioWeb servicioWeb = getDAOFactory().getServicioWebDAO().findById(fuente.getId());
				List<JSONObject> datosExtraidos = ExtractorServicioWeb.extraerTexto(servicioWeb);
				for (JSONObject docJson : datosExtraidos) {
					String textoPuro = docJson.getString("texto");
					JSONObject metadatosJson = docJson.getJSONObject("metadatos");
					
					Documento documento = guardarDocumentoBD(fuente, textoPuro, metadatosJson.toString());
					List<String> chunks = fragmentarTexto(documento.getTexto(), indexMethod, tamanoChunk, overlap);    
					
					procesarEIndexarChunks(chunks, dataset, documento, nombreColeccion, servicioEmbedding, qdrantService, metadatosJson);
				}
				}

				getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "INDEXADA", null);
			} catch (Exception e) {
				huboErrores = true;
				getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "ERROR", e.getMessage());
				compensarVectorizacion(nombreColeccion, idFuente);
			}
		}
		
		if (huboErrores) {
			return new JSONObject()
					.put("status", "partial")
					.put("mensaje", "Re-indexación completada con errores. Revise el estado y log_error de las fuentes.");
		}
		return new JSONObject().put("status", "re-indexación completa");
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));
	}

	private void compensarVectorizacion(String nombreColeccion, Integer idFuente) {
		try {
			List<Documento> documentos = getDAOFactory().getDocumentoDAO().findByIdFuente(idFuente);
			boolean vectoresCompensados = true;
			for (Documento documento : documentos) {
				try {
					new QdrantService().eliminarPorIdDocumento(nombreColeccion, documento.getId());
				} catch (Exception e) {
					vectoresCompensados = false;
					System.err.println("No se pudo compensar el vector del documento "
							+ documento.getId() + ": " + e.getMessage());
				}
			}
			if (vectoresCompensados) {
				getDAOFactory().getDocumentoDAO().deleteByIdFuente(idFuente);
			}
		} catch (Exception e) {
			System.err.println("No se pudo obtener documentos para compensación: " + e.getMessage());
		}
	}

	private Documento guardarDocumentoBD(Fuente fuente, String texto, String metadatosString) throws Exception {
	    Documento documento = new Documento();
	    documento.setFuente(fuente);
	    
	    String textoLimpio = cl.bcn.util.LimpiadorTexto.limpiar(texto);
	    documento.setTexto(textoLimpio);
	    
	    if (metadatosString != null) documento.setMetadatos(metadatosString);
	    documento.setCrdate(new Date());
	    
	    Integer idDocumento = getDAOFactory().getDocumentoDAO().insert(documento);
	    documento.setId(idDocumento);
	    return documento;
	}


	private List<String> fragmentarTexto(String texto, IndexMethod indexMethod, Integer tamanoChunk, Integer overlap) {
		if (indexMethod == null || indexMethod.getId() == null) {
			return TextSplitter.splitPorPalabras(texto, tamanoChunk, overlap);
		}
		switch (indexMethod.getId()) {
			case 1: 
				return TextSplitter.splitEstructuralAvanzado(texto, tamanoChunk, overlap);
			case 2: 
				return TextSplitter.splitPorParrafo(texto);
			case 3: 
			default:
				return TextSplitter.splitPorPalabras(texto, tamanoChunk, overlap);
		}
	}

	private void procesarEIndexarChunks(List<String> chunks, Dataset dataset, Documento documento, 
										String nombreColeccion, EmbeddingService servicioEmbedding, 
										QdrantService qdrantService, 
										JSONObject metadatosExtra) throws Exception {
		String metodoRecuperacion = (dataset.getRetrievalMethod() != null && dataset.getRetrievalMethod().getNombre() != null) 
		    ? dataset.getRetrievalMethod().getNombre().toLowerCase() : "";
		boolean esHibrida = metodoRecuperacion.contains("hibrida") || 
							metodoRecuperacion.contains("híbrida") || 
							metodoRecuperacion.contains("hybrid");
		int chunkIndex = 0; 
		for (String chunk : chunks) {
			try {
				Map<String, Object> metadatosMap = new HashMap<>();
				metadatosMap.put("texto", chunk);
				metadatosMap.put("documento_id", documento.getId());
				metadatosMap.put("chunk_index", chunkIndex);
				
				if (metadatosExtra != null) {
					for (String key : metadatosExtra.keySet()) {
						metadatosMap.put(key, metadatosExtra.get(key));
					}
				}

				String stringId = documento.getId() + "_" + chunkIndex;
				String idQdrant = java.util.UUID.nameUUIDFromBytes(stringId.getBytes(StandardCharsets.UTF_8)).toString();

				if (esHibrida) {
					
					List<Double> vdDouble = servicioEmbedding.obtenerVector(dataset.getModeloEmbedding(), chunk);
					List<Float> vectorDenso = new ArrayList<>();
					for (Double d : vdDouble) vectorDenso.add(d.floatValue());

					EmbeddingService.VectorDisperso vectorDisperso = servicioEmbedding.obtenerVectorDisperso(chunk);
					qdrantService.upsertHibrido(nombreColeccion, idQdrant, vectorDenso, vectorDisperso.getIndices(), vectorDisperso.getValores(), metadatosMap);
				} else {
					
					List<Double> vectorDensoDouble = servicioEmbedding.obtenerVector(dataset.getModeloEmbedding(), chunk);
					JSONObject metadatosJson = new JSONObject(metadatosMap);
					
					qdrantService.upsert(nombreColeccion, vectorDensoDouble, chunk, documento.getId(), chunkIndex, metadatosJson);
				}

				chunkIndex++; 
			} catch (Exception e) {
				throw new Exception("Error al vectorizar el chunk índice " + chunkIndex + " del documento " + documento.getId() + ": " + e.getMessage(), e);
			}
		}
	}
}

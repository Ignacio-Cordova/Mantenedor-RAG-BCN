package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.IndexMethod;
import cl.bcn.model.BaseDeDatos;
import cl.bcn.model.Archivo;
import java.util.Date;
import cl.bcn.model.Dataset;
import org.json.JSONObject;

import java.util.ArrayList;
import cl.bcn.util.LimpiadorTexto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Documento;
import cl.bcn.model.Fuente;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.model.ServicioWeb;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.util.EmbeddingService;
import cl.bcn.util.ExtractorJDBC;
import cl.bcn.util.ExtractorServicioWeb;
import cl.bcn.util.QdrantService;
import cl.bcn.util.TextSplitter;
/**
 * Lee nuevamente una fuente, elimina sus documentos y puntos Qdrant previos y vuelve a persistir e indexar el contenido.
 */
public class ReIndexarFuenteCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService {

	@Override
	protected JSONObject execute() throws Exception {
		Fuente fuente = getDAOFactory().getFuenteDAO().findById(getIntParam("id"));
		if (fuente == null) {
			return new JSONObject().put("error", "Fuente no encontrada");
		}
		Integer idFuente = fuente.getId();
		getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "INDEXANDO", null);
		Dataset dataset = fuente.getDataset();
		Integer tamanoChunk = dataset.getTamanoChunk();
		String nombreColeccion = dataset.getCollectionName();
		Integer overlap = dataset.getOverlap();
		if (overlap == null || overlap < 0) {
			overlap = (int) (tamanoChunk * 0.1);
		}
		IndexMethod indexMethod = dataset.getIndexMethod(); 
		ModeloEmbedding modeloEmbedding = dataset.getModeloEmbedding();	
		EmbeddingService servicioEmbedding = new EmbeddingService();
		QdrantService qdrantService = new QdrantService();
		String metodoRecuperacion = (dataset.getRetrievalMethod() != null && dataset.getRetrievalMethod().getNombre() != null) 
		        ? dataset.getRetrievalMethod().getNombre().toLowerCase() : "";

		boolean esHibrida = metodoRecuperacion.contains("hybrid") || metodoRecuperacion.contains("hibrida");
		qdrantService.crearColeccionSiNoExiste(nombreColeccion, modeloEmbedding.getDimensiones(), esHibrida);

		if ("ARCHIVO".equals(fuente.getTipo())) {
			try {
				Archivo archivo = getDAOFactory().getArchivoDAO().findById(idFuente);
				if (archivo == null || archivo.getRuta() == null) {
					throw new Exception("No existe la ruta física del archivo.");
				}

				limpiarDocumentosPrevios(idFuente, nombreColeccion, qdrantService);

				String textoCompleto = LimpiadorTexto.limpiar(
					new String(Files.readAllBytes(Paths.get(archivo.getRuta())), StandardCharsets.UTF_8));
				if (textoCompleto == null || textoCompleto.trim().isEmpty()) {
					throw new Exception("El archivo físico está vacío o no contiene texto legible.");
				}

				Documento documento = new Documento();
				documento.setFuente(fuente);
				documento.setTexto(textoCompleto);
				documento.setCrdate(new Date());
				Integer idDocumento = getDAOFactory().getDocumentoDAO().insert(documento);
				documento.setId(idDocumento);

				List<String> chunks = fragmentarTexto(textoCompleto, indexMethod, tamanoChunk, overlap);
				procesarEIndexarChunks(chunks, dataset, documento, nombreColeccion, servicioEmbedding, qdrantService, null);
				getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "INDEXADA", null);
				return new JSONObject().put("status", "success").put("mensaje", "Archivo releído desde disco y re-indexado.");
			} catch (Exception e) {
				getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "ERROR", e.getMessage());
				compensarVectorizacion(nombreColeccion, idFuente);
				throw e;
			}
		}

		try {
			limpiarDocumentosPrevios(idFuente, nombreColeccion, qdrantService);

			if ("BASE_DE_DATOS".equals(fuente.getTipo())) {
				BaseDeDatos base = getDAOFactory().getBaseDeDatosDAO().findById(idFuente);
				List<String> textos = ExtractorJDBC.extraerTextos(base);
				if (textos == null || textos.isEmpty()) {
					throw new Exception("No se pudo extraer texto de la fuente JDBC.");
				}
				for (String texto : textos) {
					Documento documento = new Documento();
					documento.setFuente(fuente);
					documento.setTexto(LimpiadorTexto.limpiar(texto));
					documento.setCrdate(new Date());
					Integer idDocumento = getDAOFactory().getDocumentoDAO().insert(documento);
					documento.setId(idDocumento);
					List<String> chunks = fragmentarTexto(documento.getTexto(), indexMethod, tamanoChunk, overlap);
					procesarEIndexarChunks(chunks, dataset, documento, nombreColeccion, servicioEmbedding, qdrantService, null);
				}
			} else if ("SERVICIO_WEB".equals(fuente.getTipo())) {
				cl.bcn.model.ServicioWeb servicio = getDAOFactory().getServicioWebDAO().findById(idFuente);
				List<JSONObject> textosExtraidos = ExtractorServicioWeb.extraerTexto(servicio);
				for (JSONObject docJson : textosExtraidos) {
					String textoPuro = docJson.getString("texto");
					JSONObject metadatosJson = docJson.getJSONObject("metadatos");
					Documento documento = new Documento();
					documento.setFuente(fuente);
					documento.setTexto(LimpiadorTexto.limpiar(textoPuro));
					documento.setMetadatos(metadatosJson.toString());
					documento.setCrdate(new Date());
					Integer idDocumento = getDAOFactory().getDocumentoDAO().insert(documento);
					documento.setId(idDocumento);
					List<String> chunks = fragmentarTexto(documento.getTexto(), indexMethod, tamanoChunk, overlap);
					procesarEIndexarChunks(chunks, dataset, documento, nombreColeccion, servicioEmbedding, qdrantService, metadatosJson);
				}
			} else {
				throw new Exception("Tipo de fuente no soportado para re-indexación: " + fuente.getTipo());
			}
			getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "INDEXADA", null);
		} catch (Exception e) {
			getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "ERROR", e.getMessage());
			compensarVectorizacion(nombreColeccion, idFuente);
			throw e;
		}
		
		JSONObject respuesta = new JSONObject();
		respuesta.put("status", "success");
		respuesta.put("mensaje", "Re-Indexación completada.");
		return respuesta;
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id"));
		validateRule(validator.validateInt("id"));		
	}

	private void compensarVectorizacion(String nombreColeccion, Integer idFuente) {
		try {
			List<Documento> documentosNuevos = getDAOFactory().getDocumentoDAO().findByIdFuente(idFuente);
			boolean vectoresCompensados = true;
			for (Documento documento : documentosNuevos) {
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

	private void limpiarDocumentosPrevios(Integer idFuente, String nombreColeccion, QdrantService qdrantService)
			throws Exception {
		List<Documento> documentosPrevios = getDAOFactory().getDocumentoDAO().findByIdFuente(idFuente);
		for (Documento documento : documentosPrevios) {
			qdrantService.eliminarPorIdDocumento(nombreColeccion, documento.getId());
		}
		if (!documentosPrevios.isEmpty()) {
			getDAOFactory().getDocumentoDAO().deleteByIdFuente(idFuente);
		}
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
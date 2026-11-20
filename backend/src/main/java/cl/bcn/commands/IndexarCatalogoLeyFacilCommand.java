package cl.bcn.commands;
import cl.bcn.model.IndexMethod;
import java.util.Date;
import cl.bcn.model.Dataset;
import cl.bcn.model.Api;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Documento;
import cl.bcn.model.Fuente;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.util.EmbeddingService;
import cl.bcn.util.QdrantService;
import cl.bcn.util.TextSplitter;
/**
 * Descarga el índice Ley Fácil, crea una fuente REST automática y vectoriza sus guías en el dataset indicado.
 */
public class IndexarCatalogoLeyFacilCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

    @Override
    protected JSONObject execute() throws Exception {
        Integer idDataset = getIntParam("id_dataset");
        String urlIndiceMaestro = getParam("url_indice"); 
        Dataset dataset = getDAOFactory().getDatasetDAO().findById(idDataset);
        String nombreColeccion = dataset.getCollectionName();
        Fuente fuentePadre = new Fuente();
        fuentePadre.setNombre("Catálogo Ley Fácil - " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        fuentePadre.setTipo("SERVICIO_WEB");
        fuentePadre.setEstadoIndexacion("INDEXANDO");
        fuentePadre.setDataset(dataset);
        fuentePadre.setCrdate(new Date()); 
        Integer idFuente = getDAOFactory().getFuenteDAO().insert(fuentePadre);
        fuentePadre.setId(idFuente);
        ModeloEmbedding modeloEmbedding = dataset.getModeloEmbedding();
        EmbeddingService servicioEmbedding = new EmbeddingService();
        QdrantService qdrantService = new QdrantService();

        String metodoRecuperacion = (dataset.getRetrievalMethod() != null && dataset.getRetrievalMethod().getNombre() != null) 
            ? dataset.getRetrievalMethod().getNombre().toLowerCase() : "";

        boolean esHibrida = metodoRecuperacion.contains("hibrida") || 
                            metodoRecuperacion.contains("híbrida") || 
                            metodoRecuperacion.contains("hybrid");
        qdrantService.crearColeccionSiNoExiste(nombreColeccion, modeloEmbedding.getDimensiones(), esHibrida);
        JSONObject jsonMaestro = obtenerJsonMaestro(urlIndiceMaestro);
        JSONArray docsArray = jsonMaestro.getJSONArray("docs");
        
        int guiasProcesadas = 0;
        boolean huboErrores = false;
        Integer overlap = dataset.getOverlap();
        if (overlap == null || overlap < 0) {
            overlap = (int) (dataset.getTamanoChunk() * 0.1);
        }
        for (int i = 0; i < docsArray.length(); i++) {
            JSONObject grupoLetra = docsArray.getJSONObject(i);
            JSONArray itemsArray = grupoLetra.getJSONArray("items");
            for (int j = 0; j < itemsArray.length(); j++) {
                JSONObject itemGuia = itemsArray.getJSONObject(j);
                String dcIdentifier = itemGuia.getString("dc_identifier");
                String urlGuiaFinal = "https://www.bcn.cl/api-leyfacil/servicio/ObtenerGuiaPublicada?uri=" + dcIdentifier;
                
                try {
                    JSONObject guiaJson = obtenerJsonMaestro(urlGuiaFinal);
                    String titulo = guiaJson.optString("titulo", "Sin Título");
                    String uri = guiaJson.optString("uri", dcIdentifier);
                    String innerJsonStr = guiaJson.optString("json", "{}");
                    JSONObject innerJson = new JSONObject(innerJsonStr);
                    String bcnNota = innerJson.optString("bcnNota", "");
                    String dcDescription = innerJson.optString("dcDescription", "");
                    String fechaActualizacion = innerJson.optString("fechaActualizacion", "");
                    List<String> listaLeyes = new ArrayList<>();
                    if (innerJson.has("leyesRelacionadas") && !innerJson.isNull("leyesRelacionadas")) {
                        JSONArray leyesArray = innerJson.getJSONArray("leyesRelacionadas");
                        for (int k = 0; k < leyesArray.length(); k++) {
                            listaLeyes.add(leyesArray.getJSONObject(k).optString("titulo"));
                        }
                    }
                    
                    List<String> listaMaterias = new ArrayList<>();
                    if (innerJson.has("materias") && !innerJson.isNull("materias")) {
                        JSONArray materiasArray = innerJson.getJSONArray("materias");
                        for (int k = 0; k < materiasArray.length(); k++) {
                            listaMaterias.add(materiasArray.getJSONObject(k).optString("descripcion"));
                        }
                    }
                    JSONObject metadatosJson = new JSONObject();
                    metadatosJson.put("titulo", titulo);
                    metadatosJson.put("uri", uri);
                    metadatosJson.put("descripcion", dcDescription);
                    metadatosJson.put("fecha_actualizacion", fechaActualizacion);
                    
                    if (!listaLeyes.isEmpty()) {
                        metadatosJson.put("leyes_relacionadas", String.join(", ", listaLeyes));
                    }
                    if (!listaMaterias.isEmpty()) {
                        metadatosJson.put("materias", String.join(", ", listaMaterias));
                    }
                    String textoLimpio = cl.bcn.util.LimpiadorTexto.limpiar(bcnNota);
                    
                    if (textoLimpio.trim().isEmpty()) {
                        System.err.println("Omitiendo " + dcIdentifier + " porque el texto está vacío.");
                        continue;
                    }
                    Documento documento = guardarDocumentoBD(fuentePadre, textoLimpio, metadatosJson.toString());
                    List<String> chunks = fragmentarTexto(documento.getTexto(), dataset.getIndexMethod(), dataset.getTamanoChunk(), overlap);
                    
                    procesarEIndexarChunks(chunks, dataset, documento, nombreColeccion, servicioEmbedding, qdrantService, metadatosJson);
                    
                    guiasProcesadas++;
                } catch (Exception e) {
                    huboErrores = true;
                    System.err.println("Omitiendo guía (" + dcIdentifier + ") por error de extracción: " + e.getMessage());
                }
            }
        }
        
        if (huboErrores) {
            getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "ERROR",
                    "Una o más guías no pudieron ser extraídas o vectorizadas.");
            compensarVectorizacion(nombreColeccion, idFuente);
            return new JSONObject()
                    .put("status", "partial")
                    .put("mensaje", "Catálogo procesado con errores. Revise el estado y log_error de la fuente.");
        }

        getDAOFactory().getFuenteDAO().actualizarEstado(idFuente, "INDEXADA", null);

        JSONObject respuesta = new JSONObject();
        respuesta.put("status", "success");
        respuesta.put("mensaje", "Catálogo procesado. Fuente ID " + idFuente + " creada automáticamente. Total de guías inyectadas a Qdrant: " + guiasProcesadas);
        return respuesta;
    }

    private JSONObject obtenerJsonMaestro(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("User-Agent", "Mantenedor-RAG-BCN/1.0");
        
        if (con.getResponseCode() != 200) {
            throw new Exception("El servidor de la BCN rechazó la conexión. Código: " + con.getResponseCode());
        }
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        
        return new JSONObject(content.toString());
    }

    private Documento guardarDocumentoBD(Fuente fuente, String texto, String metadatosString) throws Exception {
        Documento documento = new Documento();
        documento.setFuente(fuente);
        documento.setTexto(texto);
        
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
                                        QdrantService qdrantService, JSONObject metadatosExtra) throws Exception {
        
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

                    qdrantService.upsertHibrido(
                        nombreColeccion,
                        idQdrant,
                        vectorDenso,
                        vectorDisperso.getIndices(),
                        vectorDisperso.getValores(),
                        metadatosMap
                    );

                } else {
                    List<Double> vectorDensoDouble = servicioEmbedding.obtenerVector(dataset.getModeloEmbedding(), chunk);
                    JSONObject metadatosJson = new JSONObject(metadatosMap);
                    qdrantService.upsert(nombreColeccion, vectorDensoDouble, chunk, documento.getId(), chunkIndex, metadatosJson);
                }

                chunkIndex++; 
            } catch (Exception e) {
                throw new Exception("Error al vectorizar el chunk índice " + chunkIndex
                        + " del documento " + documento.getId() + ": " + e.getMessage(), e);
            }
        }
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

    @Override
    protected void validateParams() throws Exception {
        validateRule(validator.validateInt("id_dataset"));
        validateRule(validator.validateNotEmpty("url_indice"));
    }
}
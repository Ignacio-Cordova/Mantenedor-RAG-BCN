package cl.bcn.commands;
import cl.bcn.model.Archivo;
import java.util.Date;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import cacatua.business.commands.AbstractCommand;
import cacatua.business.commands.params.ArchivoUpload;
import cacatua.business.method.HTTPPostService;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.model.Fuente;
import cl.bcn.util.PhysicalFileStorage;
/**
 * Registra un archivo .txt multipart asociado a id_dataset, lo mueve al almacenamiento físico y crea su fuente PENDIENTE.
 */
public class AgregarArchivoCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService{

	
	private static final long MAXIMO_BYTES = 10L * 1024L * 1024L;
	
	@Override
    protected JSONObject execute() throws Exception {
        ArchivoUpload archivoSubido = getParamFile("file");
        if (archivoSubido == null) {
            throw new Exception("Error: El servidor no recibió ningún archivo físico.");
        }
        String idDatasetStr = getParam("id_dataset");
        Integer idDataset = Integer.valueOf(idDatasetStr);
        
        String extension = getParam("extension").toLowerCase();
        String nombreOriginal = getParam("nombre");
        if (!"txt".equals(extension)) {
            throw new Exception("Solo se permiten archivos .txt.");
        }

        Path origen = Paths.get(archivoSubido.getRutaTemporal());
        long tamanoArchivo = Files.size(origen);
        if (tamanoArchivo <= 0) {
            throw new Exception("El archivo recibido está vacío.");
        }
        if (tamanoArchivo > MAXIMO_BYTES) {
            throw new Exception("El archivo supera el límite máximo de 10 MB.");
        }
        Fuente fte = new Fuente();
        fte.setNombre(nombreOriginal);
        fte.setDataset(getDAOFactory().getDatasetDAO().findById(idDataset));
        fte.setTipo("ARCHIVO");
        fte.setEstadoIndexacion("PENDIENTE");
        fte.setCrdate(new Date());
        
        Integer idFuente = getDAOFactory().getFuenteDAO().insert(fte);
        Path rutaFinal = null;
        try {
            Path carpetaDestino = PhysicalFileStorage.getBaseDirectory().resolve("dataset_" + idDataset);
            Files.createDirectories(carpetaDestino);

            String nombreSinExtension = nombreOriginal;
            int posicionExtension = nombreSinExtension.toLowerCase().lastIndexOf(".txt");
            if (posicionExtension >= 0) {
                nombreSinExtension = nombreSinExtension.substring(0, posicionExtension);
            }
            String nombreLimpio = nombreSinExtension
                .replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ._ -]", "_")
                .replaceAll("\\s+", "_")
                .trim();
            if (nombreLimpio.isEmpty()) {
                nombreLimpio = "archivo";
            }
            rutaFinal = carpetaDestino.resolve(nombreLimpio + "_" + idFuente + ".txt");
            
            Files.move(origen, rutaFinal, StandardCopyOption.REPLACE_EXISTING);
            Archivo ar = new Archivo();
            ar.setId(idFuente);
            ar.setRuta(rutaFinal.toString());
            ar.setExtension(extension);
            
            getDAOFactory().getArchivoDAO().insert(ar);
        } catch (Exception e) {
            if (rutaFinal != null) {
                Files.deleteIfExists(rutaFinal);
            }
            getDAOFactory().getFuenteDAO().delete(idFuente);
            throw new Exception("No se pudo guardar el archivo físico: " + e.getMessage(), e);
        }
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("id_fuente", idFuente);
        response.put("mensaje", "Archivo subido y guardado en " + rutaFinal.toString());
        
        return response;
    }

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("id_dataset"));
		validateRule(validator.validateNotEmpty("extension"));
		validateRule(validator.validateNotEmpty("nombre"));
	}

}

package cl.bcn.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Resuelve y prepara el directorio físico utilizado por las fuentes ARCHIVO.
 * Permite configurar la ubicación mediante RAG_ARCHIVOS_DIR o la propiedad JVM rag.archivos.dir.
 */
public final class PhysicalFileStorage {

	private PhysicalFileStorage() {
	}

	public static Path getBaseDirectory() throws Exception {
		String configuredPath = System.getenv("RAG_ARCHIVOS_DIR");
		if (configuredPath == null || configuredPath.trim().isEmpty()) {
			configuredPath = System.getProperty("rag.archivos.dir");
		}
		if (configuredPath == null || configuredPath.trim().isEmpty()) {
			configuredPath = Paths.get(System.getProperty("user.home"), "MantenedorRAG", "archivos").toString();
		}

		Path baseDirectory = Paths.get(configuredPath).toAbsolutePath().normalize();
		Files.createDirectories(baseDirectory);
		if (!Files.isWritable(baseDirectory)) {
			throw new Exception("La carpeta física de archivos no es escribible: " + baseDirectory);
		}
		return baseDirectory;
	}
}

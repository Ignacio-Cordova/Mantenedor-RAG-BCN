package cl.bcn.util;

import java.util.UUID;
/**
 * Genera el nombre físico de colección asociado a un dataset.
 * El identificador numérico evita que dos datasets reutilicen una colección por nombre.
 */

public class QdrantNameGenerator {

    /**
     * Genera un nombre estandarizado y único para Qdrant.
     * Ejemplo: "Leyes de Chile" -> "ds_leyes_de_chile_f8a9b2"
     */
    public static String generarNombreColeccion(Integer id) {
        if (id == null) {
			id = UUID.randomUUID().hashCode();
		}
        
		return "mantenedor_rag" + "_" + id; 
    }
}
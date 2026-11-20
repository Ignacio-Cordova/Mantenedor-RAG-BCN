package cl.bcn.util;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.XML;
/**
 * Normaliza respuestas REST JSON, XML o texto plano a estructuras JSONObject.
 * La normalización permite que los extractores procesen respuestas simples y colecciones con el mismo contrato.
 */

public class TransformadorGlobal {

    public static JSONObject normalizarAJson(String contenidoCrudo, String contentType) throws Exception {
        
        String tipo = contentType != null ? contentType.toLowerCase() : "";
        String crudoLimpio = contenidoCrudo.trim();

        // XML se convierte primero porque su estructura puede representarse directamente como JSONObject.
        if (tipo.contains("xml") || crudoLimpio.startsWith("<")) {
            return XML.toJSONObject(crudoLimpio);
        }
        
        // Los objetos JSON pueden contener la fuente directamente y no requieren envoltura.
        if (crudoLimpio.startsWith("{")) {
            return new JSONObject(crudoLimpio);
        }
        
        // Los arreglos se envuelven para reutilizar la misma ruta de procesamiento de documentos.
        if (crudoLimpio.startsWith("[")) {
            JSONObject wrapper = new JSONObject();
            wrapper.put("datos", new JSONArray(crudoLimpio));
            return wrapper;
        }

        throw new Exception("El Transformador Global no reconoce el formato de origen. Content-Type: " + tipo);
    }
}
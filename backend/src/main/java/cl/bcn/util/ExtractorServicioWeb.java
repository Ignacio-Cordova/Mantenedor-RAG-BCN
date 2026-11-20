package cl.bcn.util;
import cl.bcn.model.Api;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import cl.bcn.model.ServicioWeb;
/**
 * Consume servicios REST y transforma el cuerpo recibido en documentos con texto principal y metadatos.
 * Permite rutas JSON anidadas, búsqueda recursiva y arreglos normalizados a múltiples documentos.
 */

public class ExtractorServicioWeb {
	
	public static List<JSONObject> extraerTexto(ServicioWeb servicioWeb) throws Exception {
	    String urlLimpia = servicioWeb.getUrlEndpoint().trim().replaceAll("[\\r\\n]", "");
	    URL url = new URL(urlLimpia);
	    String metodo = servicioWeb.getMetodoHttp().trim().toUpperCase();
	    
	    List<JSONObject> documentosExtraidos = new ArrayList<>();
		String headersEncriptados = servicioWeb.getHeadersJson();
		String body = servicioWeb.getBodyPayload();
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();	
		con.setRequestMethod(metodo);
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
		
		if (headersEncriptados != null && !headersEncriptados.trim().isEmpty()) {
			String jsonDesencriptado = SeguridadUtil.desencriptar(headersEncriptados);
			JSONArray arregloHeaders;
            if (jsonDesencriptado.trim().startsWith("{")) {
                arregloHeaders = new JSONArray();
                arregloHeaders.put(new JSONObject(jsonDesencriptado));
            } 
            else if (jsonDesencriptado.trim().startsWith("[")) {
                arregloHeaders = new JSONArray(jsonDesencriptado);
            } 
            else {
                throw new Exception("Formato de headers desconocido. Debe ser un JSON Object o JSON Array.");
            }
            for (int i = 0; i < arregloHeaders.length(); i++) {
                JSONObject headerItem = arregloHeaders.getJSONObject(i);
                
                String nombre = headerItem.getString("name");
                String valor = headerItem.getString("value");
                
                con.setRequestProperty(nombre, valor);
            }
        }
		if (body != null && !body.trim().isEmpty() && (metodo.equals("POST") || metodo.equals("PUT") || metodo.equals("PATCH"))) {
		    con.setDoOutput(true);
		    con.setRequestProperty("Content-Type", "application/json; utf-8");
		    try (java.io.OutputStream os = con.getOutputStream()) {
		        byte[] input = body.getBytes("utf-8");
		        os.write(input, 0, input.length);
		    }
		}
		if (con.getResponseCode() != 200) {
			InputStream errorStream = con.getErrorStream();
            if (errorStream != null) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream, "utf-8"));
                StringBuilder errorMensaje = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorMensaje.append(errorLine);
                }
                errorReader.close();
                throw new Exception("Falló la API. Código: " + con.getResponseCode() + " - Detalle del servidor: " + errorMensaje.toString());
            }
			throw new Exception("Falló la API. Código: " + con.getResponseCode());
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
        String inputLine;
        StringBuilder contenido = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            contenido.append(inputLine);
        }
        in.close();
        
        String contentType = con.getHeaderField("Content-Type");
        String respuestaCruda = contenido.toString().trim();
        JSONObject jsonNormalizado = TransformadorGlobal.normalizarAJson(respuestaCruda, contentType);
        if (jsonNormalizado.has("datos") && jsonNormalizado.optJSONArray("datos") != null) {
            JSONArray jsonArray = jsonNormalizado.getJSONArray("datos");
            
            if (jsonArray.length() == 0) {
                throw new Exception("La API devolvió un arreglo vacío []");
            }
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject elemento = jsonArray.getJSONObject(i);
                JSONObject docProcesado = procesarElemento(elemento, servicioWeb);
                if (docProcesado != null) {
                    documentosExtraidos.add(docProcesado);
                }
            }
        } else {
            JSONObject docProcesado = procesarElemento(jsonNormalizado, servicioWeb);
            
            if (docProcesado != null) {
                documentosExtraidos.add(docProcesado);
            } else {
                throw new Exception("La ruta de texto '" + servicioWeb.getRutaTexto() + "' no se encontró en la respuesta de la API.");
            }
        }
        
        return documentosExtraidos;
    }
	private static JSONObject procesarElemento(JSONObject elemento, ServicioWeb servicioWeb) {
	    JSONObject documentoExtraido = new JSONObject();
	    Object valorTextoAnidado = obtenerValorAvanzado(elemento, servicioWeb.getRutaTexto());
	    
	    if (valorTextoAnidado != null) {
	    	String textoExtraido = LimpiadorTexto.limpiar(String.valueOf(valorTextoAnidado));
	        
	        if (!textoExtraido.trim().isEmpty()) {
	            documentoExtraido.put("texto", textoExtraido);
	            
	            JSONObject metadatos = new JSONObject();
	            String rutasMeta = servicioWeb.getRutasMetadatos();
	            if (rutasMeta != null && !rutasMeta.trim().isEmpty()) {
	                String[] llavesMeta = rutasMeta.split(","); 
	                
	                for (String llave : llavesMeta) {
	                    llave = llave.trim(); 
	                    Object valorMetaAnidado = obtenerValorAnidado(elemento, llave);
	                    if (valorMetaAnidado != null) {
	                        metadatos.put(llave, valorMetaAnidado); 
	                    }
	                }
	            }
	            
	            documentoExtraido.put("metadatos", metadatos);
	            return documentoExtraido;
	        }
	    }
	    return null;
	}
	
	public static Object obtenerValorAnidado(JSONObject json, String ruta) {
	    try {
	        if (!ruta.contains(".")) {
	        	Object simpleVal = (json.has(ruta) && !json.isNull(ruta)) ? json.get(ruta) : null;
	            return desempaquetar(simpleVal);
	        }

	        String[] claves = ruta.split("\\.");
	        Object actual = json; 
	        
	        for (int i = 0; i < claves.length - 1; i++) {
	        	actual = desempaquetar(actual);
	            String clave = claves[i];
	            
	            if (actual instanceof JSONObject) {
	                JSONObject objActual = (JSONObject) actual;
	                if (objActual.has(clave) && !objActual.isNull(clave)) {
	                    actual = objActual.get(clave);
	                } else {
	                    return null;
	                }
	            } else if (actual instanceof JSONArray) {
	                JSONArray arrActual = (JSONArray) actual;
	                int index = Integer.parseInt(clave);
	                if (index < arrActual.length()) {
	                    actual = arrActual.get(index);
	                } else {
	                    return null;
	                }
	            }
	        }
	        actual = desempaquetar(actual);
	        String claveFinal = claves[claves.length - 1];
	        if (actual instanceof JSONObject) {
	            JSONObject objFinal = (JSONObject) actual;
	            if (objFinal.has(claveFinal) && !objFinal.isNull(claveFinal)) {
	                return objFinal.get(claveFinal);
	            }
	        }
	    } catch (Exception e) {
	        return null; 
	    }
	    return null;
	}
	
	public static Object obtenerValorAvanzado(JSONObject json, String ruta) {
	    if (ruta.contains(".**")) {
	        String[] partes = ruta.split("\\.\\*\\*");
	        String rutaBase = partes[0]; 
	        String llaveRecursiva = partes[1]; 
	        Object nodoBase = obtenerValorAnidado(json, rutaBase);
	        if (nodoBase != null) {
	            StringBuilder textosConcatenados = new StringBuilder();
	            buscarRecursivamente(nodoBase, llaveRecursiva, textosConcatenados);
	            return textosConcatenados.toString().trim();
	        }
	        return null;
	    }
	    return obtenerValorAnidado(json, ruta);
	}

	private static void buscarRecursivamente(Object nodo, String llaveBuscada, StringBuilder resultado) {
	    if (nodo instanceof JSONObject) {
	        JSONObject obj = (JSONObject) nodo;
	        if (obj.has(llaveBuscada) && !obj.isNull(llaveBuscada)) {
	            resultado.append(obj.get(llaveBuscada).toString()).append("\n\n");
	        }
	        java.util.Iterator<String> keys = obj.keys();
	        while (keys.hasNext()) {
	            String key = keys.next();
	            if (!key.equals(llaveBuscada)) {
	                buscarRecursivamente(obj.get(key), llaveBuscada, resultado);
	            }
	        }
	    } else if (nodo instanceof JSONArray) {
	        JSONArray arr = (JSONArray) nodo;
	        for (int i = 0; i < arr.length(); i++) {
	            buscarRecursivamente(arr.get(i), llaveBuscada, resultado);
	        }
	    }
	}
	
	private static Object desempaquetar(Object actual) {
	    if (actual instanceof String) {
	        String str = ((String) actual).trim();
	        try {
	            if (str.startsWith("{")) return new JSONObject(str);
	            if (str.startsWith("[")) return new JSONArray(str);
	        } catch (Exception e) {
	        }
	    }
	    return actual;
	}
	
}

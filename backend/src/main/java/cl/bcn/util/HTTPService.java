package cl.bcn.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Envía solicitudes HTTP POST con payload JSON mediante HttpURLConnection.
 * Centraliza la apertura de conexión, el encabezado JSON y la lectura de respuestas.
 */

public class HTTPService {
	
	public static String sendPostRequest(String urlEnviada, String jsonPayload) throws Exception {
		try {
    		URL url = new URL(urlEnviada);
    		HttpURLConnection con = (HttpURLConnection) url.openConnection();
    		
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			
			try (OutputStream os = con.getOutputStream()) {
			    byte[] input = jsonPayload.getBytes("utf-8");
			    os.write(input, 0, input.length);
			}
    		
		    int responseCode = con.getResponseCode();
		    StringBuilder response = new StringBuilder();
		    try (BufferedReader br = new BufferedReader(
		            new InputStreamReader(con.getInputStream(), "utf-8"))) {
		        String responseLine;
		        while ((responseLine = br.readLine()) != null) {
		            response.append(responseLine.trim());
		        }
		    }
		return response.toString();
		} catch (Exception e) {
			System.err.println("Error en HTTPService: " + e.getMessage());
			throw e;
		}
	}
}

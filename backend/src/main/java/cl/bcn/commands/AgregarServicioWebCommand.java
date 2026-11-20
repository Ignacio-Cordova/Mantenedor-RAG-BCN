package cl.bcn.commands;
import java.util.Date;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cacatua.business.method.HTTPPostService;
import cl.bcn.model.Fuente;
import cl.bcn.model.ServicioWeb;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.FuenteDAO;
import cl.bcn.persistence.dao.ServicioWebDAO;
import cl.bcn.util.SeguridadUtil;
/**
 * Crea una fuente REST con URL, método, headers, body, ruta de texto, metadatos y dataset asociado.
 */
public class AgregarServicioWebCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPPostService {

	@Override
	protected JSONObject execute() throws Exception {
		
		String nombre = getParam("nombre");
		String logError = getParam("log_error");
		Integer idDataset = getIntParam("id_dataset");
		
		
		String url_endpoint = getParam("url_endpoint");
		String metodo_http = getParam("metodo_http");
		String headers = getParam("headers");
		String body = getParam("body");
		String rutaTexto = getParam("ruta_texto");
		String rutasMetadatos = getParam("rutas_metadatos");
		
		
		Fuente fte = new Fuente();
	    fte.setNombre(nombre);
	    fte.setDataset(getDAOFactory().getDatasetDAO().findById(idDataset));
	    fte.setTipo("SERVICIO_WEB");
	    fte.setEstadoIndexacion("PENDIENTE");
	    fte.setCrdate(new Date());
	    
	    FuenteDAO ftedao = getDAOFactory().getFuenteDAO();
	    Integer idFuente = ftedao.insert(fte);
	    
	    ServicioWeb servicioWeb = new ServicioWeb();
	    servicioWeb.setId(idFuente);
	    servicioWeb.setUrlEndpoint(url_endpoint);
	    servicioWeb.setMetodoHttp(metodo_http);
	    servicioWeb.setBodyPayload(body);
	    servicioWeb.setRutaTexto(rutaTexto);
	    servicioWeb.setRutasMetadatos(rutasMetadatos);
	    servicioWeb.setCrdate(new Date());
	    
	    if (headers != null && !headers.trim().isEmpty()) {
	        try {
	            String headersEncriptados = SeguridadUtil.encriptar(headers);
	            
	            servicioWeb.setHeadersJson(headersEncriptados);
	            
	        } catch (Exception e) {
	            throw new Exception("Error crítico al encriptar las credenciales del servicio web", e);
	        }
	    } else {
	        servicioWeb.setHeadersJson(null);
	    }
	    
	    ServicioWebDAO servicioWebDAO = getDAOFactory().getServicioWebDAO();
	    Integer id = servicioWebDAO.insert(servicioWeb);
	    
	    JSONObject response = new JSONObject();
	    response.put("success", true);
	    response.put("message", "Servicio Web agregado exitosamente con id: " + id);
	    return response;
	    
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("nombre"));
		validateRule(validator.validateNotEmpty("url_endpoint"));
		validateRule(validator.validateNotEmpty("metodo_http"));
		validateRule(validator.validateNotEmpty("id_dataset"));
	}
	
	

}

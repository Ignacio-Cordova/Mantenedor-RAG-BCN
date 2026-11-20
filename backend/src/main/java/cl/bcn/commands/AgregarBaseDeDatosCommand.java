package cl.bcn.commands;
import cacatua.business.method.HTTPGetService;
import cl.bcn.model.BaseDeDatos;
import java.util.Date;
import cl.bcn.persistence.dao.BaseDeDatosDAO;
import org.json.JSONObject;

import cacatua.business.commands.AbstractCommand;
import cl.bcn.model.Fuente;
import cl.bcn.model.Conexion;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.persistence.dao.ConexionDAO;
import cl.bcn.persistence.dao.FuenteDAO;
/**
 * Crea una fuente PostgreSQL por consulta o tabla y reutiliza una conexión o crea una nueva con los parámetros recibidos.
 */
public class AgregarBaseDeDatosCommand extends AbstractCommand<JSONObject, PersistenceFactory> implements HTTPGetService{

@Override
protected JSONObject execute() throws Exception {
    Integer idDataset = getIntParam("id_dataset");
    String nombreFuente = getParam("nombre");
    String querySql = getParam("query_sql");
    String tabla_objetivo = getParam("tabla_objetivo");
    String modoExtraccion = getParam("modo_extraccion");
    String columna_contenido = getParam("columna_contenido");
    String id_indexacion = getParam("id_indexacion");
    String id_externo_vectorial = getParam("id_externo_vectorial");
    Integer idConexion = getIntParam("id_conexion"); 
    if (idConexion == null || idConexion == 0) {
   
        Conexion nuevaConn = new Conexion();
        nuevaConn.setNombre(getParam("nombre_conexion")); 
        nuevaConn.setHost(getParam("host"));
        nuevaConn.setUsuario(getParam("usuario"));
        nuevaConn.setPassword(getParam("password"));
        nuevaConn.setMotor(getParam("motor")); 
        nuevaConn.setPuerto(getIntParam("puerto"));
        nuevaConn.setCrdate(new Date());
      
        ConexionDAO conexionDao = getDAOFactory().getConexionDAO();
        
        idConexion = conexionDao.insert(nuevaConn); 
        
    }
    Fuente fte = new Fuente();
    fte.setNombre(nombreFuente);
    fte.setDataset(getDAOFactory().getDatasetDAO().findById(idDataset));
    fte.setTipo("BASE_DE_DATOS");
    fte.setEstadoIndexacion("PENDIENTE");
    fte.setCrdate(new Date());
    
    FuenteDAO ftedao = getDAOFactory().getFuenteDAO();
    Integer idFuente = ftedao.insert(fte);
    
    BaseDeDatos bd = new BaseDeDatos();
    bd.setId(idFuente);
    bd.setDataset(getDAOFactory().getDatasetDAO().findById(idDataset));
    bd.setConexion(getDAOFactory().getConexionDAO().findById(idConexion)); 
    bd.setQuerySql(querySql);
    bd.setTablaObjetivo(tabla_objetivo);
    bd.setModoExtraccion(modoExtraccion);
    bd.setColumnaContenido(columna_contenido);
    bd.setIdExternoVectorial(id_externo_vectorial);
    bd.setIdIndexacion(id_indexacion);

    BaseDeDatosDAO bdDao = getDAOFactory().getBaseDeDatosDAO();
    Integer idBD = bdDao.insert(bd); 
    JSONObject response = new JSONObject();
    response.put("success", true);
    response.put("id_fuente", idFuente);
    response.put("id_conexion_usada", idConexion); 
    
    return response;
	}

	@Override
	protected void validateParams() throws Exception {
		validateRule(validator.validateNotEmpty("nombre"));
		validateRule(validator.validateNotEmpty("query_sql"));
		validateRule(validator.validateNotEmpty("tabla_objetivo"));
		validateRule(validator.validateNotEmpty("modo_extraccion"));
		validateRule(validator.validateNotEmpty("columna_contenido"));
		validateRule(validator.validateNotEmpty("id_indexacion"));
		validateRule(validator.validateNotEmpty("id_externo_vectorial"));
	}
}

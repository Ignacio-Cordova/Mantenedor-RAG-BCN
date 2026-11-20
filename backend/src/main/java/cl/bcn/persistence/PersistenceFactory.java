package cl.bcn.persistence;
import cl.bcn.persistence.dao.ArchivoDAO;
import cl.bcn.persistence.dao.ApiDAO;
import cl.bcn.persistence.dao.BaseDeDatosDAO;

import java.util.HashMap;
import java.util.Map;

import cacatua.persistence.dao.impl.AbstractDAOFactory;
import cl.bcn.persistence.dao.ModeloEmbeddingDAO;
import cl.bcn.persistence.dao.ConexionDAO;
import cl.bcn.persistence.dao.DatasetDAO;
import cl.bcn.persistence.dao.DocumentoDAO;
import cl.bcn.persistence.dao.FuenteDAO;
import cl.bcn.persistence.dao.IndexMethodDAO;
import cl.bcn.persistence.dao.ModeloLlmDAO;
import cl.bcn.persistence.dao.PartidoDAO;
import cl.bcn.persistence.dao.RetrievalMethodDAO;
import cl.bcn.persistence.dao.ServicioWebDAO;
import cl.bcn.persistence.dao.ServidorVectorialDAO;


public class PersistenceFactory extends AbstractDAOFactory{

	Map<String,Object> daos;

	public PersistenceFactory() {
		daos = new HashMap<String,Object>();
		
		}
	public PartidoDAO getPartidoDAO() {
		return (PartidoDAO) daos.get(PartidoDAO.class.getCanonicalName()); 
	}
	public PartidoDAO getPartidoInstDAO() {
		return new PartidoDAO(this);
	}
	
	public ServidorVectorialDAO getServidorVectorialDAO() {
		return new ServidorVectorialDAO(this);
	}
	
	public DatasetDAO getDatasetDAO() {
		return new DatasetDAO(this);
	}


	public ApiDAO getApiDAO() {
		return new ApiDAO(this);
	}


	public ModeloLlmDAO getModeloLlmDAO() {
		return new ModeloLlmDAO(this);
	}


	public ModeloEmbeddingDAO getModeloEmbeddingDAO() {
		return new ModeloEmbeddingDAO(this);
	}


	public ArchivoDAO getArchivoDAO() {
		return new ArchivoDAO(this);
	}
	
	public BaseDeDatosDAO getBaseDeDatosDAO() {
		return new BaseDeDatosDAO(this);
	}


	public FuenteDAO getFuenteDAO() {
		return new FuenteDAO(this);
	}


	public RetrievalMethodDAO getRetrievalMethodDAO() {
		return new RetrievalMethodDAO(this);
	}


	public ConexionDAO getConexionDAO() {
		return new ConexionDAO(this);
	}


	public IndexMethodDAO getIndexMethodDAO() {
		return new IndexMethodDAO(this);
	}


	public DocumentoDAO getDocumentoDAO() {
		return new DocumentoDAO(this);
	}


	public ServicioWebDAO getServicioWebDAO() {
		return new ServicioWebDAO(this);
	}

}

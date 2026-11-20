package cl.bcn.persistence.dao;
import cl.bcn.model.Archivo;
import cacatua.persistence.dao.impl.GenericDAOJdbc;
import cl.bcn.persistence.dao.FuenteDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.PersistenceFactory;

public class FuenteDAO extends GenericDAOJdbc<PersistenceFactory, Fuente, Integer>{

	public FuenteDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(Fuente arg0) throws Exception {
		return 0;
	}
	
	public int delete(Integer idFuente) throws Exception {
	    
	    String sql = "DELETE FROM fuente WHERE id = ?"; 
	    
	    PreparedStatement psDelete = getPreparedStatement(sql);
	    
	    psDelete.setInt(1, idFuente);

	    int output = psDelete.executeUpdate();
	    
	    psDelete.close();
	    
	    return output;
	}

	@Override
	public List<Fuente> findAll() throws Exception {
		return null;
	}

	@Override
	public List<Fuente> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	@Override
	public List<Fuente> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public Fuente findById(Integer id) throws Exception {
		String sql = "SELECT * FROM fuente  " +
					 "WHERE fuente.id = ?";
		return findByQuery(sql, id);
	}
	
	public List<Fuente> findAllByDataset(Integer id_dataset) throws Exception {
		String sql = "SELECT * FROM fuente  " +
					 "LEFT JOIN archivo on archivo.id = fuente.id " +
					 "LEFT JOIN base_de_datos on base_de_datos.id = fuente.id " + 
					 "WHERE id_dataset = ?";
		return findAllByCustomQuery(sql, id_dataset);
	}

	@Override
	public Integer insert(Fuente fuente) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO fuente(nombre, tipo, estado_indexacion, log_error, id_externo_vectorial, id_indexacion,"
				+ "id_dataset, crdate)"
				+ " VALUES (?,?,?,?,?,?,?,?) RETURNING id;";
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setStringOrNull(psInsert, i++, fuente.getNombre());
		FieldParser.setStringOrNull(psInsert, i++, fuente.getTipo());
		FieldParser.setStringOrNull(psInsert, i++, fuente.getEstadoIndexacion());
		FieldParser.setStringOrNull(psInsert, i++, fuente.getLogError());
		FieldParser.setIntOrNull(psInsert, i++, fuente.getIdExternoVectorial());
		FieldParser.setIntOrNull(psInsert, i++, fuente.getIdIndexacion());
		FieldParser.setIntOrNull(psInsert, i++, fuente.getDataset().getId());
		FieldParser.setDateOrNull(psInsert, i++, fuente.getCrdate());
		
		ResultSet rs = psInsert.executeQuery();
		if(rs.next()){
			id = rs.getInt("id");
		}
		rs.close();

		psInsert.close();
		return id;
	}

	@Override
	public int update(Fuente arg0) throws Exception {
		return 0;
	}
	
	public void actualizarEstado(Integer id, String estado, String log) throws Exception {
	    Fuente fuente = findById(id);
	    String estadoActual = fuente != null ? fuente.getEstadoIndexacion() : null;
	    boolean transicionValida =
	        ("PENDIENTE".equals(estadoActual) && ("INDEXANDO".equals(estado) || "ERROR".equals(estado)))
	        || ("INDEXANDO".equals(estadoActual) && ("INDEXADA".equals(estado) || "ERROR".equals(estado)))
	        || ("ERROR".equals(estadoActual) && ("INDEXANDO".equals(estado) || "INDEXADA".equals(estado)))
	        || ("INDEXADA".equals(estadoActual) && "INDEXANDO".equals(estado))
	        || estadoActual == null;
	    if (!transicionValida) {
	        throw new Exception("Transición de estado inválida: " + estadoActual + " -> " + estado);
	    }

	    String sql = "UPDATE fuente SET estado_indexacion = ?, log_error = ? WHERE id = ?";
	    
	    PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		
		FieldParser.setStringOrNull(psInsert, i++, estado);
		FieldParser.setStringOrNull(psInsert, i++, log);
		FieldParser.setIntOrNull(psInsert, i++, id);
		
		psInsert.executeUpdate();
		psInsert.close();
	}

	@Override
	protected Fuente getAllFields(ResultSet rs) throws Exception {
		Fuente out = new Fuente();
		out.setId(rs.getInt("id"));
		out.setNombre(rs.getString("nombre"))	;
		out.setTipo(rs.getString("tipo"));
		out.setEstadoIndexacion(rs.getString("estado_indexacion"));
		out.setLogError(rs.getString("log_error"));
		out.setIdExternoVectorial(rs.getString("id_externo_vectorial"));	
		out.setIdIndexacion(rs.getString("id_indexacion"));
		out.setDataset(getDAOFactory().getDatasetDAO().findById(rs.getInt("id_dataset")));
		out.setCrdate(rs.getTimestamp("crdate"));
		return out;
	}

	@Override
	protected int getTotalRegistros() throws Exception {
		return 0;
	}

	@Override
	protected int getTotalRegistros(String arg0) throws Exception {
		return 0;
	}

}

package cl.bcn.persistence.dao;
import cl.bcn.model.Fuente;
import cl.bcn.model.Archivo;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.persistence.PersistenceFactory;

public class ArchivoDAO  extends GenericDAOJdbc<PersistenceFactory, Archivo, Integer> {

	public ArchivoDAO(PersistenceFactory df) {
		super(df);
	}
	
	public int delete(Integer id) throws Exception {
		String sql = "DELETE FROM archivo WHERE id = ?" ;
		PreparedStatement ps = getPreparedStatement(sql);

		ps.setInt(1, id);

		int output = ps.executeUpdate();
		ps.close();
		return output;
	}

	@Override
	public int delete(Archivo arg0) throws Exception {

		return 0;
	}

	@Override
	public List<Archivo> findAll() throws Exception {
		return null;
	}

	@Override
	public List<Archivo> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	public List<Archivo> findAll(Integer id_dataset, Integer limit, Integer offset) throws Exception {
		String sql = "SELECT f.*, a.ruta, a.extension FROM fuente f LEFT JOIN archivo a ON a.id = f.id "
				+ "WHERE f.tipo = 'ARCHIVO' "
				+ " AND f.id_dataset = ?  ORDER BY f.id DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, id_dataset, limit, offset);
	}
	
	public int countByDataset(Integer id_dataset) throws Exception {
	    String sql = "SELECT COUNT(*) "
	               + "FROM fuente f "
	               + "WHERE f.tipo = 'ARCHIVO' AND f.id_dataset = ?";
	               
	    return getIntValueByQuery(sql, id_dataset);
	}

	@Override
	public Archivo findById(Integer id) throws Exception {
		Archivo archivo = null;
		
		String sql = "SELECT f.id, f.nombre, f.tipo, f.estado_indexacion, f.log_error, "
				   + "f.id_externo_vectorial, f.id_indexacion, f.id_dataset, f.crdate, "
				   + "a.ruta, a.extension "
				   + "FROM fuente f "
				   + "INNER JOIN archivo a ON f.id = a.id "
				   + "WHERE f.id = ?";
		
		PreparedStatement ps = getPreparedStatement(sql);
		
		ps.setInt(1, id); 

		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			archivo = getAllFields(rs);
		}
		
		rs.close();
		ps.close();
		
		return archivo;
	}
	

	@Override
	public Integer insert(Archivo archivo) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO archivo(id, ruta, extension)"
				+ " VALUES (?,?,?) RETURNING id;";
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setIntOrNull(psInsert, i++, archivo.getId());
		FieldParser.setStringOrNull(psInsert, i++, archivo.getRuta());
		FieldParser.setStringOrNull(psInsert, i++, archivo.getExtension());
		
		ResultSet rs = psInsert.executeQuery();
		if(rs.next()){
			id = rs.getInt("id");
		}
		rs.close();

		psInsert.close();
		return id;
	}

	@Override
	public int update(Archivo arg0) throws Exception {
		return 0;
	}

	@Override
	protected Archivo getAllFields(ResultSet rs) throws Exception {
		Archivo out = new Archivo();
		out.setId(rs.getInt("id"));
		out.setNombre(rs.getString("nombre"))	;
		out.setTipo(rs.getString("tipo"));
		out.setEstadoIndexacion(rs.getString("estado_indexacion"));
		out.setLogError(rs.getString("log_error"));
		out.setIdExternoVectorial(rs.getString("id_externo_vectorial"));	
		out.setIdIndexacion(rs.getString("id_indexacion"));
		out.setDataset(getDAOFactory().getDatasetDAO().findById(rs.getInt("id_dataset")));
		out.setCrdate(rs.getTimestamp("crdate"));
		
		out.setRuta(rs.getString("ruta"));
		out.setExtension(rs.getString("extension"));
		return out;
	}

	@Override
	protected int getTotalRegistros() throws Exception {
		return 0;
	}
	
	public List<Archivo> findAllByDataset(Integer id_dataset) throws Exception {
		String sql = "SELECT f.*, a.ruta, a.extension FROM fuente f " + 
					"INNER JOIN archivo a " + 
					"ON a.id = f.id " +
					"WHERE f.id_dataset = ? AND f.tipo = 'ARCHIVO'";
		return findAllByCustomQuery(sql, id_dataset);
	}
	
	@Override
	protected int getTotalRegistros(String arg0) throws Exception {
		return 0;
	}

	@Override
	public List<Archivo> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

}

package cl.bcn.persistence.dao;
import cacatua.persistence.dao.impl.GenericDAOJdbc;
import cl.bcn.persistence.dao.DocumentoDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.model.Documento;
import cl.bcn.persistence.PersistenceFactory;

public class DocumentoDAO extends GenericDAOJdbc<PersistenceFactory, Documento, Integer>{

	public DocumentoDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(Documento id) throws Exception {
		String sql = "DELETE FROM documento WHERE id = ?" ;
		PreparedStatement ps = getPreparedStatement(sql);
		ps.setInt(1, id.getId());
		int output = ps.executeUpdate();
		ps.close();
		
		return output;
	}

	@Override
	public List<Documento> findAll() throws Exception {
		return null;
	}

	@Override
	public List<Documento> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	@Override
	public List<Documento> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}
	
	public List<Documento> findByIdFuente(Integer idFuente) throws Exception {
		String sql = "SELECT * FROM documento WHERE id_fuente = ?";
		return findAllByCustomQuery(sql, idFuente);
	}
	
	public Documento findByIdArchivo(Integer idArchivo) throws Exception {
		String sql = "SELECT * FROM documento WHERE id_fuente = ?";
		return findByQuery(sql, idArchivo);
	}

	@Override
	public Documento findById(Integer id) throws Exception {
		String sql = "SELECT * FROM documento WHERE id = ?";
		return findByQuery(sql, id);
	}
	
	public void deleteByIdBaseDeDatos(Integer idBaseDeDatos) throws Exception {
		String sql = "DELETE FROM documento WHERE id_fuente = ?"; 
	    
	    PreparedStatement psDelete = getPreparedStatement(sql);
	    
	    psDelete.setInt(1, idBaseDeDatos);

	    psDelete.executeUpdate();
	    
	    psDelete.close();
	}
	
	public void deleteByIdFuente(Integer id) throws Exception {
		String sql = "DELETE FROM documento WHERE id_fuente = ?"; 
	    
	    PreparedStatement psDelete = getPreparedStatement(sql);
	    
	    psDelete.setInt(1, id);

	    psDelete.executeUpdate();
	    
	    psDelete.close();
	}

	@Override
	public Integer insert(Documento doc) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO documento(texto, id_fuente, metadatos, crdate) VALUES (?,?,?::jsonb,?) RETURNING id;";
		
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setStringOrNull(psInsert, i++, doc.getTexto());
		FieldParser.setIntOrNull(psInsert, i++, doc.getFuente().getId());
		FieldParser.setStringOrNull(psInsert, i++, doc.getMetadatos());
		FieldParser.setTimestampOrNull(psInsert, i++, doc.getCrdate());
		
		ResultSet rs = psInsert.executeQuery();
		if (rs.next()) {
			id = rs.getInt("id");
		}
		
		rs.close();
		psInsert.close();
		return id;
		
	}

	@Override
	public int update(Documento arg0) throws Exception {
		return 0;
	}

	@Override
	protected Documento getAllFields(ResultSet rs) throws Exception {
		Documento out = new Documento();
		out.setId(rs.getInt("id"));
		out.setTexto(rs.getString("texto"));
		out.setFuente(getDAOFactory().getFuenteDAO().findById(rs.getInt("id_fuente")));
		out.setMetadatos(rs.getString("metadatos"));
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

package cl.bcn.persistence.dao;
import cl.bcn.persistence.dao.RetrievalMethodDAO;
import cacatua.persistence.dao.impl.GenericDAOJdbc;
import cl.bcn.model.RetrievalMethod;

import java.sql.ResultSet;
import java.util.List;

import cl.bcn.persistence.PersistenceFactory;

public class RetrievalMethodDAO extends GenericDAOJdbc<PersistenceFactory, RetrievalMethod, Integer>{

	public RetrievalMethodDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(RetrievalMethod arg0) throws Exception {
		return 0;
	}

	@Override
	public List<RetrievalMethod> findAll() throws Exception {
		String sql = "SELECT * FROM retrieval_method ORDER BY crdate DESC";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<RetrievalMethod> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	@Override
	public List<RetrievalMethod> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public RetrievalMethod findById(Integer id_buscado) throws Exception {
		String sql = "SELECT * FROM retrieval_method WHERE id = ?";
		return findByQuery(sql, id_buscado);
	}
	
	public RetrievalMethod findByDatasetId(Integer datasetId) throws Exception {
		return null;
	}

	@Override
	public Integer insert(RetrievalMethod arg0) throws Exception {
		return null;
	}

	@Override
	public int update(RetrievalMethod arg0) throws Exception {
		return 0;
	}

	@Override
	protected RetrievalMethod getAllFields(ResultSet rs) throws Exception {
		RetrievalMethod out = new RetrievalMethod();
		out.setId(rs.getInt("id"));
		out.setNombre(rs.getString("nombre"));
		out.setDescripcion(rs.getString("descripcion"));
		out.setCrdate(rs.getDate("crdate"));
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

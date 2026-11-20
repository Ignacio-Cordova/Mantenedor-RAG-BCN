package cl.bcn.persistence.dao;
import cl.bcn.model.IndexMethod;
import cacatua.persistence.dao.impl.GenericDAOJdbc;
import cl.bcn.persistence.dao.IndexMethodDAO;

import java.sql.ResultSet;
import java.util.List;

import cl.bcn.persistence.PersistenceFactory;

public class IndexMethodDAO extends GenericDAOJdbc<PersistenceFactory, IndexMethod, Integer>{

	public IndexMethodDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(IndexMethod arg0) throws Exception {
		return 0;
	}

	@Override
	public List<IndexMethod> findAll() throws Exception {
		String sql = ("SELECT * FROM index_method ORDER BY crdate DESC");
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<IndexMethod> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	@Override
	public List<IndexMethod> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public IndexMethod findById(Integer id_buscado) throws Exception {
		String sql = "SELECT * FROM index_method WHERE id = ?";
		return findByQuery(sql, id_buscado);
	}

	@Override
	public Integer insert(IndexMethod arg0) throws Exception {
		return null;
	}

	@Override
	public int update(IndexMethod arg0) throws Exception {
		return 0;
	}

	@Override
	protected IndexMethod getAllFields(ResultSet rs) throws Exception {
		IndexMethod out = new IndexMethod();
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

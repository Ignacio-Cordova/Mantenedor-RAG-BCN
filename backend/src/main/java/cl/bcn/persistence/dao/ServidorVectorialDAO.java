package cl.bcn.persistence.dao;
import cl.bcn.persistence.dao.ServidorVectorialDAO;
import cl.bcn.model.ServidorVectorial;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.ResultSet;
import java.util.List;

import cl.bcn.persistence.PersistenceFactory;

public class ServidorVectorialDAO extends GenericDAOJdbc<PersistenceFactory, ServidorVectorial, Integer> {

	public ServidorVectorialDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(ServidorVectorial arg0) throws Exception {
		return 0;
	}

	@Override
	public List<ServidorVectorial> findAll() throws Exception {
		String sql = "SELECT * FROM servidor_vectorial ORDER BY crdate DESC ";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<ServidorVectorial> findAll(Integer limit, Integer offset) throws Exception {
		String sql = "SELECT * FROM servidor_vectorial "
				+ "ORDER BY crdate DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, limit, offset);
	}

	@Override
	public List<ServidorVectorial> findAll(Integer limit, Integer offset, String patron) throws Exception {
		String sql = "SELECT * FROM servidor_vectorial WHERE "
				+ " proveedor ilike ?  ORDER BY crdate DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, "%"+patron+"%", limit, offset);
	}

	@Override
	public ServidorVectorial findById(Integer arg0) throws Exception {
		return null;
	}

	@Override
	public Integer insert(ServidorVectorial arg0) throws Exception {
		return null;
	}

	@Override
	public int update(ServidorVectorial arg0) throws Exception {
		return 0;
	}

	@Override
	protected ServidorVectorial getAllFields(ResultSet rs) throws Exception {
		ServidorVectorial out = new ServidorVectorial();
		out.setId(rs.getInt("id"));
		out.setProveedor(rs.getString("proveedor"));
		out.setApiKey(rs.getString("api_key"));
		out.setHostUrl(rs.getString("host_url"));
		out.setPuerto(rs.getInt("puerto"));
		out.setCrdate(rs.getTimestamp("crdate"));
		return out;
	}

	@Override
	protected int getTotalRegistros() throws Exception {
		return getIntValueByQuery("select count(*) as total from servidor_vectorial ");
	}

	@Override
	protected int getTotalRegistros(String patron) throws Exception {
		return getIntValueByQuery("select count(*) as total from servidor_vectorial "
				+ " WHERE proveedor ilike ?", "%"+patron+"%");
	}

}

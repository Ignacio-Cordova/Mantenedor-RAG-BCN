package cl.bcn.persistence.dao;
import cl.bcn.persistence.dao.ModeloEmbeddingDAO;
import cl.bcn.model.Dataset;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.model.ModeloEmbedding;
import cl.bcn.persistence.PersistenceFactory;

public class ModeloEmbeddingDAO extends GenericDAOJdbc<PersistenceFactory, ModeloEmbedding, Integer>{

	public ModeloEmbeddingDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(ModeloEmbedding modelo) throws Exception {
		return delete(modelo.getId());
	}

	public int delete(Integer id) throws Exception {
		String consulta = "SELECT COUNT(*) FROM dataset WHERE id_modelo_embedding = ?";
		PreparedStatement psConsulta = getPreparedStatement(consulta);
		psConsulta.setInt(1, id);
		ResultSet rs = psConsulta.executeQuery();
		rs.next();
		int referencias = rs.getInt(1);
		rs.close();
		psConsulta.close();
		if (referencias > 0) {
			throw new Exception("El modelo de embedding está asociado a " + referencias + " dataset(s)");
		}

		String sql = "DELETE FROM modelo_embedding WHERE id = ?";
		PreparedStatement ps = getPreparedStatement(sql);
		ps.setInt(1, id);
		int output = ps.executeUpdate();
		ps.close();
		return output;
	}

	@Override
	public List<ModeloEmbedding> findAll() throws Exception {
		String sql = "SELECT * FROM modelo_embedding ORDER BY crdate DESC";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<ModeloEmbedding> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	@Override
	public List<ModeloEmbedding> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public ModeloEmbedding findById(Integer id_buscada) throws Exception {
		String sql = "SELECT * FROM modelo_embedding WHERE id = ?";
		return findByQuery(sql, id_buscada);
	}

	@Override
	public Integer insert(ModeloEmbedding modelo) throws Exception {
		String sql = "INSERT INTO modelo_embedding(nombre_tecnico, dimensiones, max_tokens_input, id_api) "
				+ "VALUES (?, ?, ?, ?) RETURNING id";
		PreparedStatement ps = getPreparedStatement(sql);
		FieldParser.setStringOrNull(ps, 1, modelo.getNombreTecnico());
		FieldParser.setIntOrNull(ps, 2, modelo.getDimensiones());
		FieldParser.setIntOrNull(ps, 3, modelo.getMaxTokensInput());
		FieldParser.setIntOrNull(ps, 4, modelo.getApi() != null ? modelo.getApi().getId() : null);
		ResultSet rs = ps.executeQuery();
		Integer id = rs.next() ? rs.getInt("id") : null;
		rs.close();
		ps.close();
		return id;
	}

	@Override
	public int update(ModeloEmbedding modelo) throws Exception {
		String sql = "UPDATE modelo_embedding SET nombre_tecnico=?, dimensiones=?, max_tokens_input=?, id_api=? WHERE id=?";
		PreparedStatement ps = getPreparedStatement(sql);
		FieldParser.setStringOrNull(ps, 1, modelo.getNombreTecnico());
		FieldParser.setIntOrNull(ps, 2, modelo.getDimensiones());
		FieldParser.setIntOrNull(ps, 3, modelo.getMaxTokensInput());
		FieldParser.setIntOrNull(ps, 4, modelo.getApi() != null ? modelo.getApi().getId() : null);
		FieldParser.setIntOrNull(ps, 5, modelo.getId());
		int output = ps.executeUpdate();
		ps.close();
		return output;
	}

	@Override
	protected ModeloEmbedding getAllFields(ResultSet rs) throws Exception {
		ModeloEmbedding out = new ModeloEmbedding();
		out.setId(rs.getInt("id"));
		out.setNombreTecnico(rs.getString("nombre_tecnico"));
		out.setDimensiones(rs.getInt("dimensiones"));
		out.setApi(getDAOFactory().getApiDAO().findById(rs.getInt("id_api")));
		out.setMaxTokensInput(rs.getInt("max_tokens_input"));
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

package cl.bcn.persistence.dao;
import cl.bcn.persistence.dao.DatasetDAO;
import cl.bcn.model.Dataset;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.persistence.PersistenceFactory;

public class DatasetDAO extends GenericDAOJdbc<PersistenceFactory, Dataset, Integer>{

	public DatasetDAO(PersistenceFactory df) {
		super(df);
	}

	public int delete(Integer id) throws Exception {
		String sql = "DELETE FROM dataset WHERE id = ?" ;
		PreparedStatement ps = getPreparedStatement(sql);

		ps.setInt(1, id);

		int output = ps.executeUpdate();
		ps.close();
		return output;
	}

	public List<Dataset> findAll() throws Exception {
		String sql = "SELECT * FROM dataset ORDER BY crdate DESC";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<Dataset> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	@Override
	public List<Dataset> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public Dataset findById(Integer id_buscada) throws Exception {
		String sql = "SELECT * FROM dataset WHERE id = ?";
		return findByQuery(sql, id_buscada);
	}

	@Override
	public Integer insert(Dataset dts) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO dataset(nombre, tamano_chunk, overlap, collection_name, top_k, system_prompt, id_api, id_modelo_llm, id_modelo_embedding,"
				+ "id_retrieval_method, id_index_method)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?) RETURNING id;";
		
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setStringOrNull(psInsert, i++, dts.getNombre());
		FieldParser.setIntOrNull(psInsert, i++, dts.getTamanoChunk());
		FieldParser.setIntOrNull(psInsert, i++, dts.getOverlap());
		FieldParser.setStringOrNull(psInsert, i++, dts.getCollectionName());
		FieldParser.setIntOrNull(psInsert, i++, dts.getTopK());
		FieldParser.setStringOrNull(psInsert, i++, dts.getSystemPrompt());
		FieldParser.setIntOrNull(psInsert, i++, dts.getApi().getId());
		FieldParser.setIntOrNull(psInsert, i++, dts.getModeloLlm().getId());
		FieldParser.setIntOrNull(psInsert, i++, dts.getModeloEmbedding().getId());
		FieldParser.setIntOrNull(psInsert, i++, dts.getRetrievalMethod().getId());
		FieldParser.setIntOrNull(psInsert, i++, dts.getIndexMethod().getId());
		
		ResultSet rs = psInsert.executeQuery();
		if(rs.next()){
			id = rs.getInt("id");
		}
		rs.close();

		psInsert.close();
		return id;
	}

	@Override
	public int update(Dataset dataset) throws Exception {
		String sql = "UPDATE dataset SET nombre=?, tamano_chunk=?, overlap=?, collection_name=?, top_k=?, system_prompt=?, id_api=?, id_modelo_llm=?, "
					+"id_modelo_embedding=?, id_retrieval_method=?, id_index_method=? " + 
				     "WHERE id = ?;";
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setStringOrNull(psInsert, i++, dataset.getNombre());
		FieldParser.setIntOrNull(psInsert, i++, dataset.getTamanoChunk());
		FieldParser.setIntOrNull(psInsert, i++, dataset.getOverlap());
		FieldParser.setStringOrNull(psInsert, i++, dataset.getCollectionName());
		FieldParser.setIntOrNull(psInsert, i++, dataset.getTopK());
		FieldParser.setStringOrNull(psInsert, i++, dataset.getSystemPrompt());
		FieldParser.setIntOrNull(psInsert, i++, dataset.getApi().getId());
		FieldParser.setIntOrNull(psInsert, i++, dataset.getModeloLlm().getId());
		FieldParser.setIntOrNull(psInsert, i++, dataset.getModeloEmbedding().getId());
		FieldParser.setIntOrNull(psInsert, i++, dataset.getRetrievalMethod().getId());
		FieldParser.setIntOrNull(psInsert, i++, dataset.getIndexMethod().getId());
		
		FieldParser.setIntOrNull(psInsert, i++, dataset.getId());
		
		int output = psInsert.executeUpdate();
		LOG.debug("Total filas afectadas = "+output);
		psInsert.close();
		return output;
	}

	@Override
	protected Dataset getAllFields(ResultSet rs) throws Exception {
		Dataset out = new Dataset();
		out.setId(rs.getInt("id"));
		out.setNombre(rs.getString("nombre"));
		out.setTamanoChunk(rs.getInt("tamano_chunk"));
		out.setOverlap(rs.getInt("overlap"));
		out.setCollectionName(rs.getString("collection_name"));
		out.setTopK(rs.getInt("top_k"));
		out.setSystemPrompt(rs.getString("system_prompt"));
		out.setApi(getDAOFactory().getApiDAO().findById(rs.getInt("id_api")));	
		out.setModeloLlm(getDAOFactory().getModeloLlmDAO().findById(rs.getInt("id_modelo_llm")));
		out.setModeloEmbedding(getDAOFactory().getModeloEmbeddingDAO().findById(rs.getInt("id_modelo_embedding")));
		out.setRetrievalMethod(getDAOFactory().getRetrievalMethodDAO().findById(rs.getInt("id_retrieval_method")));
		out.setIndexMethod(getDAOFactory().getIndexMethodDAO().findById(rs.getInt("id_index_method")));
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

	@Override
	public int delete(Dataset arg0) throws Exception {
		return 0;
	}

}

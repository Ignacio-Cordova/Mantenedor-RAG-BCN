package cl.bcn.persistence.dao;
import cl.bcn.persistence.dao.ApiDAO;
import cl.bcn.model.Dataset;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.model.Api;
import cl.bcn.persistence.PersistenceFactory;

public class ApiDAO extends GenericDAOJdbc<PersistenceFactory, Api, Integer>{

	public ApiDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(Api arg0) throws Exception {
		return 0;
	}
	
	public int delete(Integer id) throws Exception {
		String dependenciaDataset = "SELECT COUNT(*) FROM dataset WHERE id_api = ?";
		PreparedStatement psDependenciaDataset = getPreparedStatement(dependenciaDataset);
		psDependenciaDataset.setInt(1, id);
		ResultSet rsDataset = psDependenciaDataset.executeQuery();
		rsDataset.next();
		int referenciasDataset = rsDataset.getInt(1);
		rsDataset.close();
		psDependenciaDataset.close();
		if (referenciasDataset > 0) {
			throw new Exception("El proveedor de API está asociado a " + referenciasDataset + " dataset(s) activo(s)");
		}

		String dependenciaModelos = "SELECT COUNT(*) FROM modelo_llm WHERE id_api = ?";
		PreparedStatement psDependenciaLlm = getPreparedStatement(dependenciaModelos);
		psDependenciaLlm.setInt(1, id);
		ResultSet rsLlm = psDependenciaLlm.executeQuery();
		rsLlm.next();
		int referenciasLlm = rsLlm.getInt(1);
		rsLlm.close();
		psDependenciaLlm.close();
		if (referenciasLlm > 0) {
			throw new Exception("El proveedor de API está asociado a " + referenciasLlm + " modelo(s) LLM");
		}

		String dependenciaEmbeddings = "SELECT COUNT(*) FROM modelo_embedding WHERE id_api = ?";
		PreparedStatement psDependenciaEmbedding = getPreparedStatement(dependenciaEmbeddings);
		psDependenciaEmbedding.setInt(1, id);
		ResultSet rsEmbedding = psDependenciaEmbedding.executeQuery();
		rsEmbedding.next();
		int referenciasEmbedding = rsEmbedding.getInt(1);
		rsEmbedding.close();
		psDependenciaEmbedding.close();
		if (referenciasEmbedding > 0) {
			throw new Exception("El proveedor de API está asociado a " + referenciasEmbedding + " modelo(s) de embedding");
		}

		String sql = "DELETE FROM api WHERE id = ?" ;
		PreparedStatement ps = getPreparedStatement(sql);

		ps.setInt(1, id);

		int output = ps.executeUpdate();
		ps.close();
		return output;
	}

	@Override
	public List<Api> findAll() throws Exception {
		String sql = "SELECT * FROM api ORDER BY crdate DESC";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<Api> findAll(Integer limit, Integer offset) throws Exception {
		String sql = "SELECT * from api  ORDER BY id DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, limit, offset);
	}

	@Override
	public List<Api> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public Api findById(Integer id_buscada) throws Exception {
		String sql = "SELECT * FROM api WHERE id = ?";
		return findByQuery(sql, id_buscada);
	}

	@Override
	public Integer insert(Api api) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO api(nombre_proveedor, base_url, organizacion_id, tipo_autenticacion, api_key_encriptada) "
				+ "VALUES (?, ?, ?, ?, ?) RETURNING id;";
		
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setStringOrNull(psInsert, i++, api.getNombreProveedor());
		FieldParser.setStringOrNull(psInsert, i++, api.getBaseUrl());
		FieldParser.setStringOrNull(psInsert, i++, api.getOrganizacionId());
		FieldParser.setStringOrNull(psInsert, i++, api.getTipoAutenticacion());
		FieldParser.setStringOrNull(psInsert, i++, api.getApiKeyEncriptada());
		
		ResultSet rs = psInsert.executeQuery();
		if(rs.next()){
			id = rs.getInt("id");
		}
		rs.close();

		psInsert.close();
		return id;
	}

	@Override
	public int update(Api api) throws Exception {
		String sql = "UPDATE api SET nombre_proveedor =?, base_url=?, organizacion_id=?, " 
				+ "tipo_autenticacion=?, api_key_encriptada=? WHERE id=?";
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;

		FieldParser.setStringOrNull(psInsert, i++, api.getNombreProveedor());
		FieldParser.setStringOrNull(psInsert, i++, api.getBaseUrl());
		FieldParser.setStringOrNull(psInsert, i++, api.getOrganizacionId());
		FieldParser.setStringOrNull(psInsert, i++, api.getTipoAutenticacion());
		FieldParser.setStringOrNull(psInsert, i++, api.getApiKeyEncriptada());
		
		FieldParser.setIntOrNull(psInsert, i++, api.getId());

		
		int output = psInsert.executeUpdate();
		LOG.debug("Total filas afectadas = "+output);
		psInsert.close();
		return output;
	}

	@Override
	protected Api getAllFields(ResultSet rs) throws Exception {
		Api out = new Api();
		out.setId(rs.getInt("id"));
		out.setNombreProveedor(rs.getString("nombre_proveedor"));
		out.setBaseUrl(rs.getString("base_url"));
		out.setOrganizacionId(rs.getString("organizacion_id"));
		out.setTipoAutenticacion(rs.getString("tipo_autenticacion"));
		out.setApiKeyEncriptada(rs.getString("api_key_encriptada"));
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

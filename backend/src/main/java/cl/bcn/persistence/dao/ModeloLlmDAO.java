package cl.bcn.persistence.dao;
import cl.bcn.model.ModeloLlm;
import cl.bcn.model.Dataset;
import cacatua.persistence.dao.impl.GenericDAOJdbc;
import cl.bcn.persistence.dao.ModeloLlmDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.persistence.PersistenceFactory;

public class ModeloLlmDAO extends GenericDAOJdbc<PersistenceFactory, ModeloLlm, Integer> {

	public ModeloLlmDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(ModeloLlm arg0) throws Exception {
		return 0;
	}
	
	public int delete(Integer id) throws Exception {
		String consulta = "SELECT COUNT(*) FROM dataset WHERE id_modelo_llm = ?";
		PreparedStatement psConsulta = getPreparedStatement(consulta);
		psConsulta.setInt(1, id);
		ResultSet rs = psConsulta.executeQuery();
		rs.next();
		int referencias = rs.getInt(1);
		rs.close();
		psConsulta.close();
		if (referencias > 0) {
			throw new Exception("El modelo LLM está asociado a " + referencias + " dataset(s)");
		}

		String sql = "DELETE FROM modelo_llm WHERE id = ?" ;
		PreparedStatement ps = getPreparedStatement(sql);

		ps.setInt(1, id);

		int output = ps.executeUpdate();
		ps.close();
		return output;
	}

	@Override
	public List<ModeloLlm> findAll() throws Exception {
		String sql = "SELECT * FROM modelo_llm ORDER BY crdate DESC";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<ModeloLlm> findAll(Integer limit, Integer offset) throws Exception {
		String sql = "SELECT * from modelo_llm  ORDER BY id DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, limit, offset);
	}

	@Override
	public List<ModeloLlm> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public ModeloLlm findById(Integer id_buscada) throws Exception {
		String sql = "SELECT * FROM modelo_llm WHERE id = ?";
		return findByQuery(sql, id_buscada);
	}
	
	public List<ModeloLlm> findByIdApi(Integer id_buscada) throws Exception {
		String sql = "SELECT * FROM modelo_llm WHERE id_api = ?";
		return findAllByCustomQuery(sql, id_buscada);
	}

	@Override
	public Integer insert(ModeloLlm modelo) throws Exception {
		
		Integer id = null;
		String sql = "INSERT INTO modelo_llm(nombre, nombre_tecnico, limite_tokens, id_api, crdate, endpoint, plantilla_payload, ruta_respuesta) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";
		
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		
		FieldParser.setStringOrNull(psInsert, i++, modelo.getNombre());
		FieldParser.setStringOrNull(psInsert, i++, modelo.getNombreTecnico());
		FieldParser.setIntOrNull(psInsert, i++, modelo.getLimiteTokens());
		FieldParser.setIntOrNull(psInsert, i++, modelo.getApi().getId());
		FieldParser.setStringOrNull(psInsert, i++, modelo.getEndpoint());
		FieldParser.setStringOrNull(psInsert, i++, modelo.getPlantillaPayload());
		FieldParser.setStringOrNull(psInsert, i++, modelo.getRutaRespuesta());
		
		ResultSet rs = psInsert.executeQuery();
		if(rs.next()){
			id = rs.getInt("id");
		}
		rs.close();

		psInsert.close();
		return id;
	}

	@Override
	public int update(ModeloLlm modelo) throws Exception {
		
		String sql = "UPDATE modelo_llm SET nombre=?, nombre_tecnico=?, "
				+ "limite_tokens=?, id_api=?, endpoint=?, plantilla_payload=?, ruta_respuesta=? WHERE id=? ";
		
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		
		FieldParser.setStringOrNull(psInsert, i++, modelo.getNombre());
		FieldParser.setStringOrNull(psInsert, i++, modelo.getNombreTecnico());
		FieldParser.setIntOrNull(psInsert, i++, modelo.getLimiteTokens());
		FieldParser.setIntOrNull(psInsert, i++, modelo.getApi().getId());
		FieldParser.setStringOrNull(psInsert, i++, modelo.getEndpoint());
		FieldParser.setStringOrNull(psInsert, i++, modelo.getPlantillaPayload());
		FieldParser.setStringOrNull(psInsert, i++, modelo.getRutaRespuesta());
		FieldParser.setIntOrNull(psInsert, i++, modelo.getId());
		
		int output = psInsert.executeUpdate();
		LOG.debug("Total filas afectadas = "+output);
		psInsert.close();
		return output;
	}

	@Override
	protected ModeloLlm getAllFields(ResultSet rs) throws Exception {
		ModeloLlm out = new ModeloLlm();
		out.setId(rs.getInt("id"));
		out.setNombre(rs.getString("nombre"));
		out.setNombreTecnico(rs.getString("nombre_tecnico"));
		out.setLimiteTokens(rs.getInt("limite_tokens"));
		out.setApi(getDAOFactory().getApiDAO().findById(rs.getInt("id_api")));	
		out.setCrdate(rs.getTimestamp("crdate"));
		out.setEndpoint(rs.getString("endpoint"));
		out.setPlantillaPayload(rs.getString("plantilla_payload"));
		out.setRutaRespuesta(rs.getString("ruta_respuesta"));
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

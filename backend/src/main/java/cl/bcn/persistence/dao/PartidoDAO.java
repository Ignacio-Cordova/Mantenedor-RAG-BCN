package cl.bcn.persistence.dao;
import cl.bcn.model.Partido;
import cl.bcn.persistence.dao.PartidoDAO;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.persistence.PersistenceFactory;

public class PartidoDAO extends GenericDAOJdbc<PersistenceFactory, Partido, Integer> {

	public PartidoDAO(PersistenceFactory c) {
		super(c);
	}

	@Override
	public int delete(Partido tramite) throws Exception {
		String sql = "UPDATE cdel.dim_partido SET eliminado=TRUE WHERE id = ?" ;
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setIntOrNull(psInsert, i++, tramite.getId());

		int output = psInsert.executeUpdate();
		LOG.debug("Total filas afectadas = "+output);
		psInsert.close();
		return output;
	}



	@Override
	public List<Partido> findAll() throws Exception {
		String sql = "SELECT * FROM dim_partido WHERE eliminado = 0 ORDER BY crdate DESC ";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<Partido> findAll(Integer limit, Integer offset) throws Exception {
		String sql = "SELECT * FROM dim_partido WHERE eliminado = 0 ORDER BY crdate DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, limit, offset);
	}

	@Override
	public Partido findById(Integer arg0) throws Exception {
		String sql = "SELECT * FROM dim_partido WHERE id = ? and eliminado = 0";
		return findByQuery(sql, arg0);
	}



	@Override
	public Integer insert(Partido tramite) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO cdel.dim_partido(descripcion, uri, alt_label, acronimo, url_imagen, url_imagen_110,"
				+ "id_wiki, pagina_web, tendencia_normalizada, color, carga_politica, anio_fundacion, fecha_fundacion)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id;";
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setStringOrNull(psInsert, i++, tramite.getDescripcion());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getUri());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getAcronimo());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getAltLabel());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getUrlImagen());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getUrlImagen110());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getIdWiki());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getPaginaWeb());
		FieldParser.setIntOrNull(psInsert, i++, tramite.getTendenciaNormalizada());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getColor());
		FieldParser.setIntOrNull(psInsert, i++, tramite.getCargaPolitica());
		FieldParser.setIntOrNull(psInsert, i++, tramite.getAnioFundacion());
		FieldParser.setDateOrNull(psInsert, i++, tramite.getFechaFundacion());
		
		ResultSet rs = psInsert.executeQuery();
		if(rs.next()){
			id = rs.getInt("id");
		}
		rs.close();

		psInsert.close();
		return id;
	}

	@Override
	public int update(Partido tramite) throws Exception {
		String sql = "UPDATE cdel.dim_partido SET descripcion=?, uri=?, alt_label=?, acronimo=?, url_imagen=?, url_imagen_110=?," + 
	                 "id_wiki=?, pagina_web=?, tendencia_normalizada=?, color=?, carga_politica=?, anio_fundacion=?, fecha_fundacion=?," +
				     "WHERE id = ?;";
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setStringOrNull(psInsert, i++, tramite.getDescripcion());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getUri());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getAcronimo());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getAltLabel());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getUrlImagen());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getUrlImagen110());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getIdWiki());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getPaginaWeb());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getTendenciaNormalizada());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getColor());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getCargaPolitica());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getAnioFundacion());
		FieldParser.setStringOrNull(psInsert, i++, tramite.getFechaFundacion());
		
		
		FieldParser.setIntOrNull(psInsert, i++, tramite.getId());
		
		int output = psInsert.executeUpdate();
		LOG.debug("Total filas afectadas = "+output);
		psInsert.close();
		return output;
	}

	
	private String getOtroMetodoDeEjemplo( String id) throws Exception {
		String sql = "select etiqueta from campo_anexo_lista_automatica where id = ? ";
		PreparedStatement psSelect = getPreparedStatement(sql);
		psSelect.setInt(1, Integer.valueOf(id));
		ResultSet rs = psSelect.executeQuery();
		String atributo = null;
		if(rs.next()){
			atributo = rs.getString("etiqueta");
		}
		rs.close();
		psSelect.close();
		return atributo;
	}

	
	@Override
	protected Partido getAllFields(ResultSet rs) throws Exception {
		Partido out = new Partido();
		out.setId(rs.getInt("id"));
		out.setDescripcion(rs.getString("descripcion"));
		out.setUri(rs.getString("uri"));
		out.setCrdate(rs.getTimestamp("crdate"));
		return out;
	}

	public Partido findByRandom() throws Exception {
		String sql = "SELECT * FROM dim_partido WHERE eliminado = 0 ORDER BY RANDOM() LIMIT 1";
		return findByQuery(sql);
	}
	
	public Partido findByIdBcn(String patron) throws Exception {
		String sql = "SELECT * FROM dim_partido WHERE eliminado = 0 "
				+ " AND  id_wiki LIKE ?";
		return findByQuery(sql, patron);
	}

	@Override
	public int getTotalRegistros() throws Exception {
		return getIntValueByQuery("select count(*) as total from dim_partido WHERE eliminado = 0");

	}

	@Override
	public List<Partido> findAll(Integer limit, Integer offset, String patron) throws Exception {
		String sql = "SELECT * FROM dim_partido WHERE eliminado = 0 "
				+ " AND descripcion ilike ?  ORDER BY crdate DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, "%"+patron+"%", limit, offset);
	}

	@Override
	protected int getTotalRegistros(String patron) throws Exception {
		return getIntValueByQuery("select count(*) as total from dim_partido "
				+ " WHERE eliminado = 0 AND descripcion ilike ?", "%"+patron+"%");
	}

	


}

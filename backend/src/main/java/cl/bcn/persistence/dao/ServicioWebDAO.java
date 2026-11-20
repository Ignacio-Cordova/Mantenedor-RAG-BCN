package cl.bcn.persistence.dao;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.dao.ServicioWebDAO;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cl.bcn.model.ServicioWeb;
import cl.bcn.persistence.PersistenceFactory;

public class ServicioWebDAO  extends GenericDAOJdbc<PersistenceFactory, ServicioWeb, Integer>{

	public ServicioWebDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(ServicioWeb sw) throws Exception {
		String sql = "DELETE FROM servicio_web WHERE id = ?" ;
		PreparedStatement ps = getPreparedStatement(sql);

		ps.setInt(1, sw.getId());

		int output = ps.executeUpdate();
		ps.close();
		return output;
	}

	@Override
	public List<ServicioWeb> findAll() throws Exception {
		return null;
	}

	@Override
	public List<ServicioWeb> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	@Override
	public List<ServicioWeb> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public ServicioWeb findById(Integer arg0) throws Exception {
		String sql = "SELECT f.*, sw.url_endpoint, sw.metodo_http, sw.headers_json, sw.body_payload, sw.ruta_texto, sw.rutas_metadatos FROM fuente f LEFT JOIN servicio_web sw ON sw.id = f.id "
				+ "WHERE f.tipo = 'SERVICIO_WEB' AND f.id = ? ";
		return findByQuery(sql, arg0);
	}

	@Override
	public Integer insert(ServicioWeb sw) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO servicio_web(id, url_endpoint, metodo_http, headers_json, body_payload, ruta_texto, rutas_metadatos) VALUES (?,?,?,?,?,?,?) RETURNING id;";
		
		PreparedStatement ps = getPreparedStatement(sql);
		int i = 1;
		ps.setInt(i++, sw.getId());
		ps.setString(i++, sw.getUrlEndpoint());
		ps.setString(i++, sw.getMetodoHttp());
		ps.setString(i++, sw.getHeadersJson());
		ps.setString(i++, sw.getBodyPayload());
		ps.setString(i++, sw.getRutaTexto());
		ps.setString(i++, sw.getRutasMetadatos());
		
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			id = rs.getInt("id");
		}
		rs.close();
		
		return id;
	}

	@Override
	public int update(ServicioWeb arg0) throws Exception {
		return 0;
	}
	
	public List<ServicioWeb> findAll(Integer id_dataset, Integer limit, Integer offset) throws Exception {
		String sql = "SELECT f.*, sw.url_endpoint, sw.metodo_http, sw.headers_json, sw.body_payload, sw.ruta_texto, sw.rutas_metadatos FROM fuente f LEFT JOIN servicio_web sw ON sw.id = f.id "
				+ "WHERE f.tipo = 'SERVICIO_WEB' "
				+ " AND f.id_dataset = ?  ORDER BY f.id DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, id_dataset, limit, offset);
	}
	
	public int countByDataset(Integer id_dataset) throws Exception {
	    String sql = "SELECT COUNT(*) "
	               + "FROM fuente f "
	               + "WHERE f.tipo = 'SERVICIO_WEB' AND f.id_dataset = ?";
	               
	    return getIntValueByQuery(sql, id_dataset);
	}

	@Override
	protected ServicioWeb getAllFields(ResultSet rs) throws Exception {
		ServicioWeb out = new ServicioWeb();
		out.setId(rs.getInt("id"));
		out.setUrlEndpoint(rs.getString("url_endpoint"));
		out.setMetodoHttp(rs.getString("metodo_http"));
		out.setHeadersJson(rs.getString("headers_json"));
		out.setBodyPayload(rs.getString("body_payload"));
		out.setRutaTexto(rs.getString("ruta_texto"));
		out.setRutasMetadatos(rs.getString("rutas_metadatos"));
		
		out.setNombre(rs.getString("nombre"))	;
		out.setTipo(rs.getString("tipo"));
		out.setEstadoIndexacion(rs.getString("estado_indexacion"));
		out.setLogError(rs.getString("log_error"));
		out.setIdExternoVectorial(rs.getString("id_externo_vectorial"));	
		out.setIdIndexacion(rs.getString("id_indexacion"));
		out.setDataset(getDAOFactory().getDatasetDAO().findById(rs.getInt("id_dataset")));
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

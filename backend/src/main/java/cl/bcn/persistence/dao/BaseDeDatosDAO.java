package cl.bcn.persistence.dao;
import cl.bcn.model.BaseDeDatos;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.dao.BaseDeDatosDAO;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.persistence.PersistenceFactory;

public class BaseDeDatosDAO extends GenericDAOJdbc<PersistenceFactory, BaseDeDatos, Integer> {
	
	
	public int delete(Integer id) throws Exception {
		String sql = "DELETE FROM base_de_datos WHERE id = ?" ;
		PreparedStatement ps = getPreparedStatement(sql);

		ps.setInt(1, id);

		int output = ps.executeUpdate();
		ps.close();
		return output;
	}
	
	public List<BaseDeDatos> findAll(Integer id_dataset, Integer limit, Integer offset) throws Exception {
		String sql = "SELECT f.*, bd.tabla_objetivo, bd.query_sql, bd.modo_extraccion, bd.columna_contenido, bd.id_conexion FROM fuente f LEFT JOIN base_de_datos bd ON bd.id = f.id "
				+ "WHERE f.tipo = 'BASE_DE_DATOS' "
				+ " AND f.id_dataset = ?  ORDER BY f.id DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, id_dataset, limit, offset);
	}
	
	public int countByDataset(Integer id_dataset) throws Exception {
	    String sql = "SELECT COUNT(*) "
	               + "FROM fuente f "
	               + "WHERE f.tipo = 'BASE_DE_DATOS' AND f.id_dataset = ?";
	               
	    return getIntValueByQuery(sql, id_dataset);
	}
	
	public BaseDeDatosDAO(PersistenceFactory df) {
		super(df);
	}

	@Override
	public int delete(BaseDeDatos arg0) throws Exception {
		return 0;
	}

	@Override
	public List<BaseDeDatos> findAll() throws Exception {
		String sql = "SELECT f.*, bd.tabla_objetivo, bd.query_sql, bd.modo_extraccion, bd.columna_contenido, bd.id_conexion FROM fuente f LEFT JOIN base_de_datos bd ON bd.id = f.id "
				+ "WHERE f.tipo = 'BASE_DE_DATOS' ORDER BY f.id DESC";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<BaseDeDatos> findAll(Integer arg0, Integer arg1) throws Exception {
		return null;
	}

	@Override
	public List<BaseDeDatos> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

	@Override
	public BaseDeDatos findById(Integer arg0) throws Exception {
		String sql = "SELECT f.*, bd.tabla_objetivo, bd.query_sql, bd.modo_extraccion, bd.columna_contenido, bd.id_conexion FROM fuente f LEFT JOIN base_de_datos bd ON bd.id = f.id "
				+ "WHERE f.tipo = 'BASE_DE_DATOS' AND f.id = ? ";
		return findByQuery(sql, arg0);
	}

	@Override
	public Integer insert(BaseDeDatos bd) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO base_de_datos(id, tabla_objetivo, query_sql, modo_extraccion, columna_contenido,"
				+ "id_conexion)"
				+ " VALUES (?,?,?,?,?,?) RETURNING id;";
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setIntOrNull(psInsert, i++, bd.getId());
		FieldParser.setStringOrNull(psInsert, i++, bd.getTablaObjetivo());
		FieldParser.setStringOrNull(psInsert, i++, bd.getQuerySql());
		FieldParser.setStringOrNull(psInsert, i++, bd.getModoExtraccion());
		FieldParser.setStringOrNull(psInsert, i++, bd.getColumnaContenido());
		FieldParser.setIntOrNull(psInsert, i++, bd.getConexion().getId());
		
		ResultSet rs = psInsert.executeQuery();
		if(rs.next()){
			id = rs.getInt("id");
		}
		rs.close();

		psInsert.close();
		return id;
	}

	@Override
	public int update(BaseDeDatos arg0) throws Exception {
		return 0;
	}

	@Override
	protected BaseDeDatos getAllFields(ResultSet rs) throws Exception {
		BaseDeDatos out = new BaseDeDatos();
		out.setId(rs.getInt("id"));
		out.setNombre(rs.getString("nombre"))	;
		out.setTipo(rs.getString("tipo"));
		out.setEstadoIndexacion(rs.getString("estado_indexacion"));
		out.setLogError(rs.getString("log_error"));
		out.setIdExternoVectorial(rs.getString("id_externo_vectorial"));	
		out.setIdIndexacion(rs.getString("id_indexacion"));
		out.setDataset(getDAOFactory().getDatasetDAO().findById(rs.getInt("id_dataset")));
		out.setCrdate(rs.getTimestamp("crdate"));
		
		if (hasColumn(rs, "tabla_objetivo")) {
			out.setTablaObjetivo(rs.getString("tabla_objetivo"));
		}
		if (hasColumn(rs, "query_sql")) {
			out.setQuerySql(rs.getString("query_sql"));
		}
		if (hasColumn(rs, "columna_contenido")) {	
			out.setColumnaContenido(rs.getString("columna_contenido"));
		}		
		out.setModoExtraccion(rs.getString("modo_extraccion"));
		out.setConexion(getDAOFactory().getConexionDAO().findById(rs.getInt("id_conexion")));
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

package cl.bcn.persistence.dao;
import cl.bcn.model.Fuente;
import cl.bcn.persistence.dao.ConexionDAO;
import cacatua.persistence.dao.impl.GenericDAOJdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import cacatua.persistence.util.FieldParser;
import cl.bcn.model.Conexion;
import cl.bcn.persistence.PersistenceFactory;
import cl.bcn.util.SeguridadUtil;

public class ConexionDAO extends GenericDAOJdbc<PersistenceFactory, Conexion, Integer> {

	public ConexionDAO(PersistenceFactory df) {
		super(df);
	}

	public int delete(Integer id) throws Exception {
		String consulta = "SELECT COUNT(*) FROM base_de_datos WHERE id_conexion = ?";
		PreparedStatement psConsulta = getPreparedStatement(consulta);
		psConsulta.setInt(1, id);
		ResultSet rs = psConsulta.executeQuery();
		rs.next();
		int referencias = rs.getInt(1);
		rs.close();
		psConsulta.close();
		if (referencias > 0) {
			throw new Exception("La conexión está asociada a " + referencias + " fuente(s) de base de datos");
		}

		String sql = "DELETE FROM conexion WHERE id = ?" ;
		PreparedStatement ps = getPreparedStatement(sql);

		ps.setInt(1, id);

		int output = ps.executeUpdate();
		ps.close();
		return output;
	}

	@Override
	public List<Conexion> findAll() throws Exception {
		String sql = "SELECT * FROM conexion ORDER BY crdate DESC";
		return findAllByCustomQuery(sql);
	}

	@Override
	public List<Conexion> findAll(Integer limit, Integer offset) throws Exception {
		String sql = "SELECT * from conexion  ORDER BY id DESC LIMIT ? OFFSET ? ";
		return findAllByCustomQuery(sql, limit, offset);
	}

	@Override
	public Conexion findById(Integer id) throws Exception {
		String sql = "SELECT * FROM conexion WHERE id = ?";
		return findByQuery(sql, id);
	}

	@Override
	public Integer insert(Conexion conexion) throws Exception {
		Integer id = null;
		String sql = "INSERT INTO conexion(nombre, puerto, password, usuario, host, "
				+ "motor, crdate)"
				+ " VALUES (?,?,?,?,?,?,?) RETURNING id;";
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		FieldParser.setStringOrNull(psInsert, i++, conexion.getNombre());
		FieldParser.setIntOrNull(psInsert, i++, conexion.getPuerto());
		FieldParser.setStringOrNull(psInsert, i++, encriptarPassword(conexion.getPassword()));
		FieldParser.setStringOrNull(psInsert, i++, conexion.getUsuario());
		FieldParser.setStringOrNull(psInsert, i++, conexion.getHost());
		FieldParser.setStringOrNull(psInsert, i++, conexion.getMotor());
		FieldParser.setDateOrNull(psInsert, i++, conexion.getCrdate());
		
		ResultSet rs = psInsert.executeQuery();
		if(rs.next()){
			id = rs.getInt("id");
		}
		rs.close();

		psInsert.close();
		return id;
	}

	@Override
	public int update(Conexion conexion) throws Exception {
		String sql = "UPDATE conexion SET nombre=?, puerto=?, password=?, usuario=?, host=?, motor=? "
					+ "WHERE id=?;";
		
		PreparedStatement psInsert = getPreparedStatement(sql);
		int i = 1;
		
		FieldParser.setStringOrNull(psInsert, i++, conexion.getNombre());
		FieldParser.setIntOrNull(psInsert, i++, conexion.getPuerto());
		FieldParser.setStringOrNull(psInsert, i++, encriptarPassword(conexion.getPassword()));
		FieldParser.setStringOrNull(psInsert, i++, conexion.getUsuario());
		FieldParser.setStringOrNull(psInsert, i++, conexion.getHost());
		FieldParser.setStringOrNull(psInsert, i++, conexion.getMotor());
		
		FieldParser.setIntOrNull(psInsert, i++, conexion.getId());
		
		int output = psInsert.executeUpdate();
		LOG.debug("Total filas afectadas = "+output);
		psInsert.close();
		return output;
		
	}

	@Override
	protected Conexion getAllFields(ResultSet rs) throws Exception {
		Conexion out = new Conexion();
		out.setId(rs.getInt("id"));
		out.setNombre(rs.getString("nombre"));
		out.setPuerto(rs.getInt("puerto"));
		out.setPassword(desencriptarPassword(rs.getString("password")));
		out.setUsuario(rs.getString("usuario"));	
		out.setHost(rs.getString("host"));
		out.setMotor(rs.getString("motor"));
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
	public int delete(Conexion arg0) throws Exception {
		return 0;
	}

	private String encriptarPassword(String password) throws Exception {
		if (password == null || password.isEmpty()) {
			return password;
		}
		return SeguridadUtil.encriptar(password);
	}

	private String desencriptarPassword(String password) throws Exception {
		if (password == null || password.isEmpty()) {
			return password;
		}
		try {
			return SeguridadUtil.desencriptar(password);
		} catch (java.security.GeneralSecurityException e) {
			return password;
		} catch (IllegalArgumentException e) {
			return password;
		}
	}

	@Override
	public List<Conexion> findAll(Integer arg0, Integer arg1, String arg2) throws Exception {
		return null;
	}

}

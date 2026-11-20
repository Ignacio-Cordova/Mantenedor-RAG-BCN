package cl.bcn.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cacatua.configuration.IConfiguration;
import cacatua.persistence.postgresql.PostgreSQLPersistenceUtil;
import cacatua.util.Loggerable;

public class ConfiguracionPersistente implements IConfiguration, Loggerable {

	@Override
	public List<Map<String, String>> getProperties() {
		LinkedList<Map<String, String>> out = new LinkedList<Map<String, String>> ();
		try {
			Connection conexion = PostgreSQLPersistenceUtil.getConexion();

			String sql = "SELECT * from configuracion where eliminado = false ";
			PreparedStatement psSelect;
			psSelect = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			ResultSet rs = psSelect.executeQuery();
			while (rs.next()) {
				Map<String, String> d = new HashMap<String, String>();
				d.put(rs.getString("clave"), rs.getString("valor"));
				out.add(d);
			}
			rs.close();
			psSelect.close();
			conexion.close();
			LOG.info("OK carga de configuracion desde BD");
			LOG.info(out);;
		} catch(Exception e) {
			LOG.error("ERROR cargando configuración dede BD", e);;
		}
		return out;
	}

}

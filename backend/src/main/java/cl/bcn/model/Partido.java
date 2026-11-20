package cl.bcn.model;
import cl.bcn.model.Partido;
import java.util.Date;

public class Partido {

	private Integer id;
	private String descripcion;
	private String uri;
	private String acronimo;
	private String alt_label;
	private String url_imagen;
	private String url_imagen_110;
	private String id_wiki;
	private Integer tendencia_normalizada;
	private String pagina_web;
	private String color;
	private Integer carga_politica;
	private Integer anio_fundacion;
	private Integer eliminado;
	private Date fecha_fundacion;
	private Date crdate;
	private Date lastUpdate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAnioFundacion() {
		return anio_fundacion;
	}
	public void setAnioFundacion(Integer anioFundacion) {
		this.anio_fundacion = anioFundacion;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getAcronimo() {
		return acronimo;
	}
	public void setAcronimo(String acronimo) {
		this.acronimo = acronimo;
	}
	public String getUrlImagen() {
		return url_imagen;
	}
	public void setUrlImagen(String urlImagen) {
		this.url_imagen = urlImagen;
	}
	public String getUrlImagen110() {
		return url_imagen_110;
	}
	public void setUrlImagen110(String urlImagen110) {
		this.url_imagen_110 = urlImagen110;
	}
	public String getAltLabel() {
		return alt_label;
	}
	public void setAltLabel(String AltLabel) {
		this.alt_label = AltLabel;
	}
	public String getIdWiki() {
		return id_wiki;
	}
	public void setIdWiki(String IdWiki) {
		this.id_wiki = IdWiki;
	}
	public Integer getTendenciaNormalizada() {
		return tendencia_normalizada;
	}
	public void setTendenciaNormalizada(Integer tendencia) {
		this.tendencia_normalizada = tendencia;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getPaginaWeb() {
		return pagina_web;
	}
	public void setPaginaWeb(String pagina) {
		this.pagina_web = pagina;
	}
	public Integer getCargaPolitica() {
		return carga_politica;
	}
	public void setCargaPolitica(Integer carga) {
		this.carga_politica = carga;
	}
	public Date getFechaFundacion() {
		return fecha_fundacion;
	}
	public void setFechaFundacion(Date FechaFundacion) {
		this.fecha_fundacion = FechaFundacion;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public Date getCrdate() {
		return crdate;
	}
	public void setCrdate(Date crdate) {
		this.crdate = crdate;
	}
	public Integer getEliminado() {
		return eliminado;
	}
	public void setEliminado(Integer eliminado) {
		this.eliminado = eliminado;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fecha_fundacion == null) ? 0 : fecha_fundacion.hashCode());
		result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
		result = prime * result + ((eliminado == null) ? 0 : eliminado.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((acronimo == null) ? 0 : acronimo.hashCode());
		result = prime * result + ((alt_label == null) ? 0 : alt_label.hashCode());
		result = prime * result + ((anio_fundacion == null) ? 0 : anio_fundacion.hashCode());
		result = prime * result + ((url_imagen == null) ? 0 : url_imagen.hashCode());
		result = prime * result + ((url_imagen_110 == null) ? 0 : url_imagen_110.hashCode());
		result = prime * result + ((tendencia_normalizada == null) ? 0 : tendencia_normalizada.hashCode());
		result = prime * result + ((pagina_web == null) ? 0 : pagina_web.hashCode());
		result = prime * result + ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((crdate == null) ? 0 : crdate.hashCode());
		result = prime * result + ((id_wiki == null) ? 0 : id_wiki.hashCode());
		result = prime * result + ((carga_politica == null) ? 0 : carga_politica.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Partido other = (Partido) obj;
		if (fecha_fundacion == null) {
			if (other.fecha_fundacion != null)
				return false;
		} else if (!fecha_fundacion.equals(other.fecha_fundacion))
			return false;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (eliminado == null) {
			if (other.eliminado != null)
				return false;
		} else if (!eliminado.equals(other.eliminado))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (acronimo == null) {
			if (other.acronimo != null)
				return false;
		} else if (!acronimo.equals(other.acronimo))
			return false;
		if (alt_label == null) {
			if (other.alt_label != null)
				return false;
		} else if (!alt_label.equals(other.alt_label))
			return false;
		if (url_imagen == null) {
			if (other.url_imagen != null)
				return false;
		} else if (!url_imagen.equals(other.url_imagen))
			return false;
		if (url_imagen_110 == null) {
			if (other.url_imagen_110 != null)
				return false;
		} else if (!url_imagen_110.equals(other.url_imagen_110))
			return false;
		if (anio_fundacion == null) {
			if (other.anio_fundacion != null)
				return false;
		} else if (!anio_fundacion.equals(other.anio_fundacion))
			return false;
		if (carga_politica == null) {
			if (other.carga_politica != null)
				return false;
		} else if (!carga_politica.equals(other.carga_politica))
			return false;
		if (id_wiki == null) {
			if (other.id_wiki != null)
				return false;
		} else if (!id_wiki.equals(other.id_wiki))
			return false;
		if (crdate == null) {
			if (other.crdate != null)
				return false;
		} else if (!crdate.equals(other.crdate))
			return false;
		if (pagina_web == null) {
			if (other.pagina_web != null)
				return false;
		} else if (!pagina_web.equals(other.pagina_web))
			return false;
		if (crdate == null) {
			if (other.crdate != null)
				return false;
		} else if (!crdate.equals(other.crdate))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (tendencia_normalizada == null) {
			if (other.tendencia_normalizada != null)
				return false;
		} else if (!tendencia_normalizada.equals(other.tendencia_normalizada))
			return false;
		return true;
	}
	
}

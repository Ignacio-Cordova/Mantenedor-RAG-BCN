package cl.bcn.model;
import cl.bcn.model.Fuente;
import cl.bcn.model.Archivo;

import java.util.Objects;

public class Archivo extends Fuente {
	String ruta;
	String extension;
	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(extension, ruta);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Archivo other = (Archivo) obj;
		return Objects.equals(extension, other.extension) && Objects.equals(ruta, other.ruta);
	}
	
}
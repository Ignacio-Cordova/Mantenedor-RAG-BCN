package cl.bcn.model;
import cl.bcn.model.Documento;
import cl.bcn.model.Fuente;
import java.util.Date;

import java.util.Objects;

public class Documento {

    private Integer id;
    private String texto;
    private Date crdate;
    private Fuente fuente; 
    private String metadatos;

    public Documento() {}
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Date getCrdate() { return crdate; }
    public void setCrdate(Date crdate) { this.crdate = crdate; }

    public Fuente getFuente() { return fuente; }
    public void setFuente(Fuente fuente) { this.fuente = fuente; }
    
    public String getMetadatos() { return metadatos; }
    public void setMetadatos(String metadatos) { this.metadatos = metadatos; }
    
    public Integer getIdDataset() {
        return (fuente != null) ? fuente.getId() : null;
    }

	@Override
	public int hashCode() {
		return Objects.hash(crdate, fuente, id, metadatos, texto);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Documento other = (Documento) obj;
		return Objects.equals(crdate, other.crdate) && Objects.equals(fuente, other.fuente)
				&& Objects.equals(id, other.id) && Objects.equals(metadatos, other.metadatos)
				&& Objects.equals(texto, other.texto);
	}
    

    
}
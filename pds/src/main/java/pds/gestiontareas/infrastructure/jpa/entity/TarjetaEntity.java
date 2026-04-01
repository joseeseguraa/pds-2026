package pds.gestiontareas.infrastructure.jpa.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tarjetas")
public class TarjetaEntity {
    
    @Id
    private String id;
    
    private String titulo;
    private String descripcion;
    private boolean completada;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tarjetas_etiquetas", joinColumns = @JoinColumn(name = "tarjeta_id"))
    private List<EtiquetaEmbeddable> etiquetas = new ArrayList<>();

    public TarjetaEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public List<EtiquetaEmbeddable> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<EtiquetaEmbeddable> etiquetas) { this.etiquetas = etiquetas; }
}
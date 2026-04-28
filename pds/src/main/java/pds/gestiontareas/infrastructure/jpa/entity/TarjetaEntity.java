package pds.gestiontareas.infrastructure.jpa.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "tarjetas")
public class TarjetaEntity {
    
    @Id
    private String id;
    
    private String tipo;
    private String titulo;
    private String descripcion;
    private boolean completada;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tarjetas_etiquetas", joinColumns = @JoinColumn(name = "tarjeta_id"))
    private List<EtiquetaEmbeddable> etiquetas = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tarjetas_checklists", joinColumns = @JoinColumn(name = "tarjeta_id"))
    private List<ItemChecklistEmbeddable> checklist = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tarjetas_visitadas", joinColumns = @JoinColumn(name = "tarjeta_id"))
    @Column(name = "nombre_lista_visitada")
    private List<String> listasVisitadas = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tarjetas_permisos", joinColumns = @JoinColumn(name = "tarjeta_id"))
    @MapKeyColumn(name = "email_usuario")
    @Column(name = "nivel_permiso")
    private Map<String, String> permisosUsuarios = new HashMap<>();

    public Map<String, String> getPermisosUsuarios() { return permisosUsuarios; }
    public void setPermisosUsuarios(Map<String, String> permisosUsuarios) { this.permisosUsuarios = permisosUsuarios; }
    
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

    public List<ItemChecklistEmbeddable> getChecklist() { return checklist; }
    public void setChecklist(List<ItemChecklistEmbeddable> checklist) { this.checklist = checklist; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public List<String> getListasVisitadas() { return listasVisitadas; }
    public void setListasVisitadas(List<String> listasVisitadas) { this.listasVisitadas = listasVisitadas; }
}
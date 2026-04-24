package pds.gestiontareas.infrastructure.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tableros")
public class TableroEntity {
    
    @Id
    private String id;
    
    private String nombre;
    private String emailCreador; 
    private boolean bloqueado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "tablero_id")
    @OrderColumn(name = "orden_lista")
    private List<ListaTareasEntity> listas = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tableros_historial", joinColumns = @JoinColumn(name = "tablero_id"))
    private List<TrazaAccionEmbeddable> historial = new ArrayList<>();
    
    public TableroEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmailCreador() { return emailCreador; }
    public void setEmailCreador(String emailCreador) { this.emailCreador = emailCreador; }

    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }

    public List<ListaTareasEntity> getListas() { return listas; }
    public void setListas(List<ListaTareasEntity> listas) { this.listas = listas; }
    
    public List<TrazaAccionEmbeddable> getHistorial() { return historial; }
    public void setHistorial(List<TrazaAccionEmbeddable> historial) { this.historial = historial; }
}
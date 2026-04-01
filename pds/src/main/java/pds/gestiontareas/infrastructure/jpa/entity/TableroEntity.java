package pds.gestiontareas.infrastructure.jpa.entity;

import jakarta.persistence.CascadeType;
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
    private List<ListaTareasEntity> listas = new ArrayList<>();

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
}
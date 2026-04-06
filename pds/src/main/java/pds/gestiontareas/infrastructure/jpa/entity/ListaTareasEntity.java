package pds.gestiontareas.infrastructure.jpa.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "listas")
public class ListaTareasEntity {

    @Id
    private String id;
    
    private String nombre;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "listas_tarjetas_ids", joinColumns = @JoinColumn(name = "lista_id"))
    @Column(name = "tarjeta_id")
    private Set<String> tarjetasIds = new LinkedHashSet<>();
    
    @Column(nullable = true)
    private Integer limiteTarjetas;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "listas_precedentes", joinColumns = @JoinColumn(name = "lista_id"))
    @Column(name = "nombre_lista_requerida")
    private List<String> listasPrecedentesRequeridas = new ArrayList<>();

    public ListaTareasEntity() {}

    // --- Getters y Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getTarjetasIds() { 
        return new ArrayList<>(tarjetasIds); 
    }
    public void setTarjetasIds(List<String> tarjetasIds) { 
        this.tarjetasIds = new LinkedHashSet<>(tarjetasIds != null ? tarjetasIds : new ArrayList<>()); 
    }
    
    public Integer getLimiteTarjetas() { return limiteTarjetas; }
    public void setLimiteTarjetas(Integer limiteTarjetas) { this.limiteTarjetas = limiteTarjetas; }

    public List<String> getListasPrecedentesRequeridas() { return listasPrecedentesRequeridas; }
    public void setListasPrecedentesRequeridas(List<String> listasPrecedentesRequeridas) { this.listasPrecedentesRequeridas = listasPrecedentesRequeridas; }
}
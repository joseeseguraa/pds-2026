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
import java.util.List;

@Entity
@Table(name = "listas")
public class ListaTareasEntity {

    @Id
    private String id;
    
    private String nombre;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "listas_tarjetas_ids", joinColumns = @JoinColumn(name = "lista_id"))
    @Column(name = "tarjeta_id")
    private List<String> tarjetasIds = new ArrayList<>();

    public ListaTareasEntity() {}

    // --- Getters y Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getTarjetasIds() { return tarjetasIds; }
    public void setTarjetasIds(List<String> tarjetasIds) { this.tarjetasIds = tarjetasIds; }
}
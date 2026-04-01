package pds.gestiontareas.domain.model.tablero.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListaTareas {
    private final String id;
    private String titulo;
    private List<String> tarjetasIds;

    public ListaTareas(String titulo) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.tarjetasIds = new ArrayList<>();
    }
    
    public ListaTareas(String id, String titulo, List<String> tarjetasIds) {
        this.id = id;
        this.titulo = titulo;
        
        if (tarjetasIds != null) {
            this.tarjetasIds = new ArrayList<>(tarjetasIds);
        } else {
            this.tarjetasIds = new ArrayList<>();
        }
    }
    
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    
    public void cambiarTitulo(String nuevoTitulo) {
        this.titulo = nuevoTitulo;
    }

    public void añadirTarjeta(String tarjetaId) {
        this.tarjetasIds.add(tarjetaId);
    }

    public void quitarTarjeta(String tarjetaId) {
        this.tarjetasIds.remove(tarjetaId);
    }

    public List<String> getTarjetasIds() {
        return tarjetasIds;
    }
}
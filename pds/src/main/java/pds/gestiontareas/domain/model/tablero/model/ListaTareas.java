package pds.gestiontareas.domain.model.tablero.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListaTareas {
    private final String id;
    private String titulo;
    private List<String> tarjetasIds;
    private Integer limiteTarjetas = null;
    private List<String> listasPrecedentesRequeridas = new ArrayList<>();

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
    
    public void setLimiteTarjetas(Integer limiteTarjetas) { 
    	this.limiteTarjetas = limiteTarjetas; 
    }

    public List<String> getTarjetasIds() {
        return tarjetasIds;
    }
    
    public Integer getLimiteTarjetas() { 
    	return limiteTarjetas; 
    }
    
    public List<String> getListasPrecedentesRequeridas() { 
    	return listasPrecedentesRequeridas; 
    }
    
    public void añadirListaRequerida(String nombreLista) { 
    	this.listasPrecedentesRequeridas.add(nombreLista); 
    }
    
    public void validarReglasEntrada(pds.gestiontareas.domain.model.tarjeta.model.Tarjeta tarjeta, String nombreListaOrigen) {
        if (this.limiteTarjetas != null && this.tarjetasIds.size() >= this.limiteTarjetas) {
            throw new IllegalStateException("Límite alcanzado. La lista '" + this.titulo + "' tiene un límite de " + this.limiteTarjetas + " tareas.");
        }
        
        if (nombreListaOrigen != null && !this.listasPrecedentesRequeridas.isEmpty()) {
            for (String listaRequerida : this.listasPrecedentesRequeridas) {
                if (!nombreListaOrigen.equals(listaRequerida) && !tarjeta.haVisitado(listaRequerida)) {
                    throw new IllegalStateException("Flujo inválido. Para entrar a '" + this.titulo + "', la tarea debe haber pasado antes por '" + listaRequerida + "'.");
                }
            }
        }
    }
}
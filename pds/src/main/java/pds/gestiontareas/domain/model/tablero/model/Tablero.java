package pds.gestiontareas.domain.model.tablero.model;

import pds.gestiontareas.domain.model.tablero.id.TableroId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Agregado Raíz: Tablero.
 * DECISIÓN DE DISEÑO (DDD): Se ha decidido no modelar el "Usuario" como un Agregado 
 * independiente. Dado que los requisitos solo exigen un email para la creación 
 * y no hay gestión de perfiles o autenticación compleja, el creador se modela 
 * simplemente como un String asociado a este Tablero, 
 * respetando el principio YAGNI (You Aren't Gonna Need It).
 */

public class Tablero {
    private TableroId id;
    private String nombre;
    private String creador;
    private boolean bloqueado;
    private List<ListaTareas> listas;
    private List<TrazaAccion> historial;

    public Tablero(String nombre, String creador) {
        this.id = new TableroId();
        this.nombre = nombre;
        this.creador = creador;
        this.bloqueado = false;
        this.listas = new ArrayList<>();
        this.historial = new ArrayList<>();
        
        this.listas.add(new ListaTareas("Completadas"));
        
        registrarAccion("Tablero '" + nombre + "' creado por " + creador);
    }
    
    public Tablero(TableroId id, String nombre, String creador, boolean bloqueado, List<ListaTareas> listas) {
        this.id = id;
        this.nombre = nombre;
        this.creador = creador;
        this.bloqueado = bloqueado;
        this.listas = listas;
        this.historial = new ArrayList<>();
    }

    public void bloquear() {
        this.bloqueado = true;
        registrarAccion("El tablero ha sido bloqueado.");
    }

    public void desbloquear() {
        this.bloqueado = false;
        registrarAccion("El tablero ha sido desbloqueado.");
    }

    public void añadirLista(String tituloLista) {
        ListaTareas nuevaLista = new ListaTareas(tituloLista);
        this.listas.add(nuevaLista);
        registrarAccion("Añadida nueva lista: " + tituloLista);
    }

    public void añadirTarjetaALista(String tarjetaId, String listaId) {
        if (this.bloqueado) {
            throw new IllegalStateException("No se pueden añadir tarjetas, el tablero está bloqueado temporalmente.");
        }
        
        ListaTareas lista = buscarListaPorId(listaId)
            .orElseThrow(() -> new IllegalArgumentException("La lista no existe en este tablero"));
            
        lista.añadirTarjeta(tarjetaId);
        registrarAccion("Tarjeta añadida a la lista: " + lista.getTitulo());
    }

    private Optional<ListaTareas> buscarListaPorId(String listaId) {
        return listas.stream().filter(l -> l.getId().equals(listaId)).findFirst();
    }
    
    private void registrarAccion(String descripcion) {
        this.historial.add(new TrazaAccion(descripcion));
    }
    
    public void moverTarjeta(String tarjetaId, String listaOrigenId, String listaDestinoId) {
        ListaTareas origen = buscarListaPorId(listaOrigenId)
                .orElseThrow(() -> new IllegalArgumentException("La lista de origen no existe"));
                
        ListaTareas destino = buscarListaPorId(listaDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("La lista de destino no existe"));

        origen.quitarTarjeta(tarjetaId);
        destino.añadirTarjeta(tarjetaId);

        registrarAccion("Tarjeta movida de '" + origen.getTitulo() + "' a '" + destino.getTitulo() + "'");
    }
    
    public void eliminarLista(String nombreLista) {
        boolean removida = this.listas.removeIf(lista -> lista.getTitulo().equals(nombreLista));
        if (removida) {
            registrarAccion("Se eliminó la lista completa '" + nombreLista + "'.");
        }
    }

    public void eliminarTarjetaDeLista(String nombreLista, String tarjetaId) {
        for (ListaTareas lista : this.listas) {
            if (lista.getTitulo().equals(nombreLista)) {
                lista.getTarjetasIds().remove(tarjetaId);
                registrarAccion("Se eliminó una tarjeta de la lista '" + nombreLista + "'.");
                break;
            }
        }
    }
    
    public TableroId getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isBloqueado() { return bloqueado; }
    public List<ListaTareas> getListas() { return listas; }
    public List<TrazaAccion> getHistorial() { return historial; }
    public String getCreador() { return creador; }
}
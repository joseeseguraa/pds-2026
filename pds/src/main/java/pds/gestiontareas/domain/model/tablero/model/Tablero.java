package pds.gestiontareas.domain.model.tablero.model;

import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.usuario.model.Email;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Tablero {
    private TableroId id;
    private String nombre;
    @SuppressWarnings("unused")
    private Email creador;
    private boolean bloqueado;
    private List<ListaTareas> listas;
    private List<TrazaAccion> historial;

    public Tablero(String nombre, Email creador) {
        this.id = new TableroId();
        this.nombre = nombre;
        this.creador = creador;
        this.bloqueado = false;
        this.listas = new ArrayList<>();
        this.historial = new ArrayList<>();
        
        this.listas.add(new ListaTareas("Completadas"));
        
        registrarAccion("Tablero '" + nombre + "' creado por " + creador.getDireccion());
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
    
    public void moverTarjetaACompletadas(String tarjetaId, String listaOrigenId) {
        ListaTareas listaCompletadas = listas.stream()
                .filter(l -> l.getTitulo().equals("Completadas"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encuentra la lista de Completadas"));

        ListaTareas origen = buscarListaPorId(listaOrigenId)
                .orElseThrow(() -> new IllegalArgumentException("La lista de origen no existe"));

        origen.quitarTarjeta(tarjetaId);
        listaCompletadas.añadirTarjeta(tarjetaId);

        registrarAccion("Tarjeta marcada como completada y movida a la lista 'Completadas'");
    }

    private Optional<ListaTareas> buscarListaPorId(String listaId) {
        return listas.stream().filter(l -> l.getId().equals(listaId)).findFirst();
    }
    
    private void registrarAccion(String descripcion) {
        this.historial.add(new TrazaAccion(descripcion));
    }
    
    public TableroId getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isBloqueado() { return bloqueado; }
    public List<ListaTareas> getListas() { return listas; }
    public List<TrazaAccion> getHistorial() { return historial; }
}
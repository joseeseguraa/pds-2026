package pds.gestiontareas.domain.model.tablero.model;

import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.id.ListaTareasId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tablero {

    private TableroId id;
    private String nombre;
    private String creador;
    private boolean bloqueado;
    private List<ListaTareas> listas;
    private List<TrazaAccion> historial;
    private List<String> usuariosCompartidos;

    public Tablero(String nombre, String creador) {
        this.id = new TableroId(UUID.randomUUID().toString());
        this.nombre = nombre;
        this.creador = creador;
        this.bloqueado = false;
        this.listas = new ArrayList<>();
        this.historial = new ArrayList<>();
        this.usuariosCompartidos = new ArrayList<>();
        registrarAccionEnHistorial("Tablero creado por " + creador);
    }

    public Tablero(TableroId id, String nombre, String creador, boolean bloqueado, List<ListaTareas> listas, List<String> usuariosCompartidos) {
        this.id = id;
        this.nombre = nombre;
        this.creador = creador;
        this.bloqueado = bloqueado;
        this.listas = listas;
        this.historial = new ArrayList<>();
        this.usuariosCompartidos = new ArrayList<>(usuariosCompartidos);
    }

    protected Tablero() {}

    public void bloquear() { this.bloqueado = true; registrarAccionEnHistorial("Tablero bloqueado"); }
    public void desbloquear() { this.bloqueado = false; registrarAccionEnHistorial("Tablero desbloqueado"); }
    public void añadirLista(String tituloLista) { validarTableroNoBloqueado("No se pueden añadir listas en un tablero bloqueado."); this.listas.add(new ListaTareas(tituloLista)); registrarAccionEnHistorial("Se añadió la lista: " + tituloLista); }
    public void eliminarLista(String tituloLista) { validarTableroNoBloqueado("No se pueden eliminar listas en un tablero bloqueado."); this.listas.removeIf(l -> l.getTitulo().equals(tituloLista)); registrarAccionEnHistorial("Se eliminó la lista: " + tituloLista); }
    public void añadirTarjetaALista(String tarjetaId, ListaTareasId listaId) { validarTableroNoBloqueado("No se pueden añadir nuevas tarjetas en un tablero bloqueado."); ListaTareas lista = obtenerListaPorId(listaId); lista.añadirTarjeta(tarjetaId); registrarAccionEnHistorial("Tarjeta " + tarjetaId + " añadida a la lista " + lista.getTitulo()); }
    public void moverTarjeta(String tarjetaId, ListaTareasId origenId, ListaTareasId destinoId) { ListaTareas origen = obtenerListaPorId(origenId); ListaTareas destino = obtenerListaPorId(destinoId); origen.quitarTarjeta(tarjetaId); destino.añadirTarjeta(tarjetaId); registrarAccionEnHistorial("Tarjeta " + tarjetaId + " movida de " + origen.getTitulo() + " a " + destino.getTitulo()); }
    public void eliminarTarjetaDeLista(String nombreLista, String tarjetaId) { validarTableroNoBloqueado("No se pueden eliminar tarjetas en un tablero bloqueado."); ListaTareas lista = this.listas.stream().filter(l -> l.getTitulo().equals(nombreLista)).findFirst().orElseThrow(() -> new IllegalArgumentException("Lista no encontrada")); lista.quitarTarjeta(tarjetaId); registrarAccionEnHistorial("Tarjeta " + tarjetaId + " eliminada de la lista " + nombreLista); }
    public void compartirCon(String email) { if (email == null || email.trim().isEmpty()) { throw new IllegalArgumentException("El email no puede estar vacío"); } if (!this.usuariosCompartidos.contains(email)) { this.usuariosCompartidos.add(email); registrarAccionEnHistorial("Tablero compartido con el usuario: " + email); } }
    public void registrarAccionEnHistorial(String mensaje) { this.historial.add(new TrazaAccion(mensaje)); }
    public void limpiarHistorial() { this.historial.clear(); registrarAccionEnHistorial("Se vació el historial de acciones manualmente."); }
    private void validarTableroNoBloqueado(String mensajeError) { if (this.bloqueado) { throw new IllegalStateException(mensajeError); } }
    private ListaTareas obtenerListaPorId(ListaTareasId listaId) { return this.listas.stream().filter(l -> l.getId().equals(listaId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Lista no encontrada en el tablero.")); }
    
    public TableroId getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCreador() { return creador; }
    public boolean isBloqueado() { return bloqueado; }
    public List<ListaTareas> getListas() { return new ArrayList<>(listas); } 
    public List<TrazaAccion> getHistorial() { return new ArrayList<>(historial); }
    public List<String> getUsuariosCompartidos() { return new ArrayList<>(usuariosCompartidos); }
}
package pds.gestiontareas.domain.model.tablero.model;

import pds.gestiontareas.domain.model.tablero.id.ListaTareasId;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListaTareas {

    private ListaTareasId id;
    private String titulo;
    private List<String> tarjetasIds;
    private Integer limiteTarjetas;
    private List<String> listasPrecedentesRequeridas;

    // Constructor estándar
    public ListaTareas(String titulo) {
        this.id = new ListaTareasId(UUID.randomUUID().toString());
        this.titulo = titulo;
        this.tarjetasIds = new ArrayList<>();
        this.listasPrecedentesRequeridas = new ArrayList<>();
        this.limiteTarjetas = null;
    }

    // Constructor de reconstrucción para la infraestructura (JPA)
    public ListaTareas(ListaTareasId id, String titulo, List<String> tarjetasIds) {
        this.id = id;
        this.titulo = titulo;
        // Aseguramos que la lista sea mutable
        this.tarjetasIds = new ArrayList<>(tarjetasIds); 
        this.listasPrecedentesRequeridas = new ArrayList<>();
        this.limiteTarjetas = null;
    }

    protected ListaTareas() {}

    // --- MÉTODOS DE NEGOCIO (DDD) ---

    public void añadirTarjeta(String tarjetaId) {
        if (limiteTarjetas != null && tarjetasIds.size() >= limiteTarjetas) {
            throw new IllegalStateException("No se puede añadir la tarjeta. La lista '" + titulo + "' ha alcanzado su límite de " + limiteTarjetas + " tarjetas.");
        }
        this.tarjetasIds.add(tarjetaId);
    }

    public void quitarTarjeta(String tarjetaId) {
        this.tarjetasIds.remove(tarjetaId);
    }

    public void validarReglasEntrada(Tarjeta tarjeta, String nombreListaOrigen) {
        if (limiteTarjetas != null && tarjetasIds.size() >= limiteTarjetas) {
            throw new IllegalStateException("Límite de tarjetas alcanzado en la lista destino.");
        }

        if (tarjeta != null && !listasPrecedentesRequeridas.isEmpty()) {
            for (String requerida : listasPrecedentesRequeridas) {
                if (!tarjeta.haVisitadoLista(requerida) && !requerida.equals(nombreListaOrigen)) {
                    throw new IllegalStateException("La tarjeta debe pasar por la lista '" + requerida + "' antes de entrar a '" + this.titulo + "'.");
                }
            }
        }
    }

    // --- REGLAS DE LA LISTA ---

    public void setLimiteTarjetas(Integer limite) {
        if (limite != null && limite < 0) {
            throw new IllegalArgumentException("El límite de tarjetas no puede ser negativo.");
        }
        this.limiteTarjetas = limite;
    }

    public void añadirListaRequerida(String nombreLista) {
        if (!this.listasPrecedentesRequeridas.contains(nombreLista)) {
            this.listasPrecedentesRequeridas.add(nombreLista);
        }
    }

    public void limpiarListasRequeridas() {
        this.listasPrecedentesRequeridas.clear();
    }

    // --- GETTERS ---
    
    public ListaTareasId getId() { return id; }
    public String getTitulo() { return titulo; }
    public Integer getLimiteTarjetas() { return limiteTarjetas; }
    public List<String> getTarjetasIds() { return new ArrayList<>(tarjetasIds); }
    public List<String> getListasPrecedentesRequeridas() { return new ArrayList<>(listasPrecedentesRequeridas); }
}
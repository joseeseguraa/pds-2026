package pds.gestiontareas.domain.model.tarjeta.model;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Tarjeta {

    protected TarjetaId id;
    protected String titulo;
    protected String descripcion;
    protected boolean completada;
    protected List<Etiqueta> etiquetas;
    protected List<String> historialVisitas; 
    protected Map<String, PermisoAcceso> permisosUsuarios;

    public Tarjeta(String titulo, String descripcion) {
        this.id = new TarjetaId(UUID.randomUUID().toString());
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
        this.etiquetas = new ArrayList<>();
        this.historialVisitas = new ArrayList<>();
        this.permisosUsuarios = new HashMap<>();
    }

    protected Tarjeta() {}

    // --- MÉTODOS DE NEGOCIO (DDD) ---

    public void cambiarDescripcion(String nuevaDescripcion) {
        this.descripcion = nuevaDescripcion;
    }

    public void completar() {
        this.completada = true;
    }

    public void reabrir() {
        this.completada = false;
    }

    public void añadirEtiqueta(Etiqueta etiqueta) {
        if (!tieneEtiqueta(etiqueta.getColorHex())) {
            this.etiquetas.add(etiqueta);
        }
    }

    public void quitarEtiqueta(String colorHex) {
        this.etiquetas.removeIf(e -> e.getColorHex().equals(colorHex));
    }

    public boolean tieneEtiqueta(String colorHex) {
        return this.etiquetas.stream().anyMatch(e -> e.getColorHex().equals(colorHex));
    }

    public void registrarVisita(String nombreLista) {
        if (!this.historialVisitas.contains(nombreLista)) {
            this.historialVisitas.add(nombreLista);
        }
    }

    public boolean haVisitadoLista(String nombreLista) {
        return this.historialVisitas.contains(nombreLista);
    }

    public void asignarPermiso(String email, PermisoAcceso permiso) {
        this.permisosUsuarios.put(email, permiso);
    }

    public boolean puedeLeer(String emailUsuario, String emailDueño) {
        if (emailUsuario.equalsIgnoreCase(emailDueño)) return true;
        return permisosUsuarios.containsKey(emailUsuario); 
    }

    public boolean puedeEscribir(String emailUsuario, String emailDueño) {
        if (emailUsuario.equalsIgnoreCase(emailDueño)) return true;
        return permisosUsuarios.get(emailUsuario) == PermisoAcceso.ESCRITURA;
    }

    // --- MÉTODOS PARA CHECKLIST (Comportamiento por defecto) ---

    public void añadirItemChecklist(String texto) {
        throw new UnsupportedOperationException("Esta tarjeta no soporta checklists.");
    }

    public void actualizarEstadoItemChecklist(String textoItem, boolean estaCompletado) {
        throw new UnsupportedOperationException("Esta tarjeta no soporta checklists.");
    }

    public void eliminarItemChecklist(String textoItem) {
        throw new UnsupportedOperationException("Esta tarjeta no soporta checklists.");
    }

    // --- GETTERS & SETTERS ---
    
    public TarjetaId getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
    
    public List<Etiqueta> getEtiquetas() { return new ArrayList<>(etiquetas); } 
    public List<String> getHistorialVisitas() { return new ArrayList<>(historialVisitas); }
    public Map<String, PermisoAcceso> getPermisosUsuarios() { return new HashMap<>(permisosUsuarios); }
}
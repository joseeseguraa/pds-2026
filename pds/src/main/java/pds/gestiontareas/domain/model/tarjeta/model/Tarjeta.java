package pds.gestiontareas.domain.model.tarjeta.model;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Tarjeta {
    protected TarjetaId id;
    protected String titulo;
    protected String descripcion;
    protected boolean completada;
    protected List<Etiqueta> etiquetas;
    protected List<String> listasVisitadas;
    protected Map<String, PermisoAcceso> permisosUsuarios = new HashMap<>();

    public Tarjeta(String titulo, String descripcion) {
        this.id = new TarjetaId();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
        this.etiquetas = new ArrayList<>();
        this.listasVisitadas = new ArrayList<>();
    }

    public void marcarCompletada() { this.completada = true; }
    public void setCompletada(boolean estado) { this.completada = estado; }
    public void añadirEtiqueta(Etiqueta etiqueta) { this.etiquetas.add(etiqueta); }
    public String getDescripcion() { return descripcion; }
    public void cambiarDescripcion(String nuevaDescripcion) { this.descripcion = nuevaDescripcion; }
    public List<Etiqueta> getEtiquetas() { return etiquetas; }
    public boolean tieneEtiqueta(String colorHex) { return etiquetas.stream().anyMatch(e -> e.getColor().equals(colorHex)); }
    public void quitarEtiqueta(String colorHex) { etiquetas.removeIf(e -> e.getColor().equals(colorHex)); }
    public TarjetaId getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isCompletada() { return completada; }
    public List<String> getListasVisitadas() { return listasVisitadas; }
    public Map<String, PermisoAcceso> getPermisosUsuarios() { return permisosUsuarios; }
    public void setPermisosUsuarios(Map<String, PermisoAcceso> permisosUsuarios) { this.permisosUsuarios = permisosUsuarios; }
    
    public void registrarVisita(String nombreLista) {
        if (!this.listasVisitadas.contains(nombreLista)) {
            this.listasVisitadas.add(nombreLista);
        }
    }
    
    public boolean haVisitado(String nombreLista) {
        return this.listasVisitadas.contains(nombreLista);
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
}
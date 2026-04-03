package pds.gestiontareas.domain.model.tarjeta.model;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import java.util.ArrayList;
import java.util.List;

public abstract class Tarjeta {
    protected TarjetaId id;
    protected String titulo;
    protected String descripcion;
    protected boolean completada;
    protected List<Etiqueta> etiquetas;

    private List<ItemChecklist> checklist = new ArrayList<>();
    
    public Tarjeta(String titulo, String descripcion) {
        this.id = new TarjetaId();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
        this.etiquetas = new ArrayList<>();
    }

    public void marcarCompletada() {
        this.completada = true;
    }

    public void añadirEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }
    
    public void cambiarDescripcion(String nuevaDescripcion) { 
        this.descripcion = nuevaDescripcion; 
    }
    
    public List<Etiqueta> getEtiquetas() {
        return etiquetas;
    }
    
    public boolean tieneEtiqueta(String colorHex) {
        return etiquetas.stream().anyMatch(e -> e.getColor().equals(colorHex));
    }

    public void quitarEtiqueta(String colorHex) {
        etiquetas.removeIf(e -> e.getColor().equals(colorHex));
    }

    public TarjetaId getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isCompletada() { return completada; }
    public List<ItemChecklist> getChecklist() { return checklist; }
    public void setChecklist(java.util.List<ItemChecklist> checklist) { this.checklist = checklist; }
}
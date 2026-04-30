package pds.gestiontareas.application.dto;

import java.util.List;

public class TarjetaDTO {
    private String id;
    private String titulo;

    private String descripcion;
    private boolean completada;
    private boolean esChecklist;
    private List<String> coloresEtiquetas;
    private List<ItemChecklistDTO> itemsChecklist;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
    public boolean isEsChecklist() { return esChecklist; }
    public void setEsChecklist(boolean esChecklist) { this.esChecklist = esChecklist; }
    public List<String> getColoresEtiquetas() { return coloresEtiquetas; }
    public void setColoresEtiquetas(List<String> coloresEtiquetas) { this.coloresEtiquetas = coloresEtiquetas; }
    public List<ItemChecklistDTO> getItemsChecklist() { return itemsChecklist; }
    public void setItemsChecklist(List<ItemChecklistDTO> itemsChecklist) { this.itemsChecklist = itemsChecklist; }
}
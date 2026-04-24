package pds.gestiontareas.application.dto;

import java.util.List;

public class TarjetaDTO {
    private String id;
    private String titulo;
    private boolean completada;
    private List<String> coloresEtiquetas;

    public TarjetaDTO(String id, String titulo, boolean completada, List<String> coloresEtiquetas) {
        this.id = id;
        this.titulo = titulo;
        this.completada = completada;
        this.coloresEtiquetas = coloresEtiquetas;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isCompletada() { return completada; }
    public List<String> getColoresEtiquetas() { return coloresEtiquetas; }
}
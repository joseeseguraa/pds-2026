package pds.gestiontareas.application.dto;

import java.util.List;

public class TableroDTO {
    private String id;
    private String nombre;
    private boolean bloqueado;
    private List<ListaDTO> listas;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }
    public List<ListaDTO> getListas() { return listas; }
    public void setListas(List<ListaDTO> listas) { this.listas = listas; }
}
package pds.gestiontareas.application.dto;

import java.util.List;

public class TableroDTO {
    private String id;
    private String nombre;
    private boolean bloqueado;
    private List<ListaDTO> listas;

    public TableroDTO(String id, String nombre, boolean bloqueado, List<ListaDTO> listas) {
        this.id = id;
        this.nombre = nombre;
        this.bloqueado = bloqueado;
        this.listas = listas;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isBloqueado() { return bloqueado; }
    public List<ListaDTO> getListas() { return listas; }
}
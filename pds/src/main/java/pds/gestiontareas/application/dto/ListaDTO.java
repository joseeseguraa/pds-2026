package pds.gestiontareas.application.dto;

import java.util.List;

public class ListaDTO {
    private String id;
    private String titulo;
    private List<String> tarjetasIds; 

    public ListaDTO(String id, String titulo, List<String> tarjetasIds) {
        this.id = id;
        this.titulo = titulo;
        this.tarjetasIds = tarjetasIds;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public List<String> getTarjetasIds() { return tarjetasIds; }
}
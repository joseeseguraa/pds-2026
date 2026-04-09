package pds.gestiontareas.application.dto.plantilla;

import java.util.List;

public class TableroPlantillaDTO {
    private String nombre;
    private List<ListaPlantillaDTO> listas;
    
    // Getters y Setters necesarios para Jackson
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<ListaPlantillaDTO> getListas() { return listas; }
    public void setListas(List<ListaPlantillaDTO> listas) { this.listas = listas; }
}
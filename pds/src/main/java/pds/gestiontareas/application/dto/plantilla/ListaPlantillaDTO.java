package pds.gestiontareas.application.dto.plantilla;
import java.util.List;

public class ListaPlantillaDTO {
    private String nombre;
    private Integer limite; // Puede ser null si no tiene límite
    private List<TarjetaPlantillaDTO> tarjetas;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getLimite() { return limite; }
    public void setLimite(Integer limite) { this.limite = limite; }
    public List<TarjetaPlantillaDTO> getTarjetas() { return tarjetas; }
    public void setTarjetas(List<TarjetaPlantillaDTO> tarjetas) { this.tarjetas = tarjetas; }
}
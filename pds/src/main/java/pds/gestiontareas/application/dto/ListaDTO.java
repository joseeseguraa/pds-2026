package pds.gestiontareas.application.dto;

import java.util.List;

public class ListaDTO {

    private String nombre;
    private Integer limite;
    private List<TarjetaDTO> tarjetas;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getLimite() { return limite; }
    public void setLimite(Integer limite) { this.limite = limite; }
    public List<TarjetaDTO> getTarjetas() { return tarjetas; }
    public void setTarjetas(List<TarjetaDTO> tarjetas) { this.tarjetas = tarjetas; }
}
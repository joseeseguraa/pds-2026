package pds.gestiontareas.domain.model.tarjeta.model;

public class Etiqueta {
    private final String nombre;
    private final String color;

    public Etiqueta(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
    }

    public String getNombre() { return nombre; }
    public String getColor() { return color; }
}
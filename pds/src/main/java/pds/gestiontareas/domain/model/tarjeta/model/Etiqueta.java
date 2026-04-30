package pds.gestiontareas.domain.model.tarjeta.model;

import java.util.Objects;

public class Etiqueta {
    private String nombre;
    private String colorHex;

    public Etiqueta(String nombre, String colorHex) {
        if (colorHex == null || colorHex.trim().isEmpty()) {
            throw new IllegalArgumentException("El color hexadecimal es obligatorio");
        }
        this.nombre = nombre;
        this.colorHex = colorHex;
    }

    // Constructor vacío protegido, requerido por JPA si usas bases de datos relacionales
    protected Etiqueta() {}

    public String getNombre() { 
        return nombre; 
    }
    
    public String getColorHex() { 
        return colorHex; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Etiqueta etiqueta = (Etiqueta) o;
        return Objects.equals(nombre, etiqueta.nombre) && 
               Objects.equals(colorHex, etiqueta.colorHex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, colorHex);
    }
}
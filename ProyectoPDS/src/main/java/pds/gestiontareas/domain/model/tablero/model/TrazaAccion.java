package pds.gestiontareas.domain.model.tablero.model;

import java.time.LocalDateTime;

public class TrazaAccion {
    private final String descripcion;
    private final LocalDateTime fecha;

    public TrazaAccion(String descripcion) {
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
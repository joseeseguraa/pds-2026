package pds.gestiontareas.infrastructure.jpa.entity;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class TrazaAccionEmbeddable {

    private String descripcion;
    private LocalDateTime fecha;

    public TrazaAccionEmbeddable() {}

    public TrazaAccionEmbeddable(String descripcion, LocalDateTime fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
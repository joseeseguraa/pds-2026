package pds.gestiontareas.domain.model.tarjeta.model;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import java.util.ArrayList;

public class TarjetaTarea extends Tarjeta {

    // Constructor estándar
    public TarjetaTarea(String titulo, String descripcion) {
        super(titulo, descripcion);
    }

    // Constructor de reconstrucción para la infraestructura (JPA)
    public TarjetaTarea(TarjetaId id, String titulo, String descripcion) {
        super();
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
        this.etiquetas = new ArrayList<>();
        this.historialVisitas = new ArrayList<>();
    }

    protected TarjetaTarea() {
        super();
    }
}
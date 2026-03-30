package pds.gestiontareas.domain.model.tarjeta.model;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import java.util.ArrayList;
import java.util.List;

public abstract class Tarjeta {
    protected TarjetaId id;
    protected String titulo;
    protected String descripcion;
    protected boolean completada;
    protected List<Etiqueta> etiquetas;

    public Tarjeta(String titulo, String descripcion) {
        this.id = new TarjetaId();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
        this.etiquetas = new ArrayList<>();
    }

    public void marcarCompletada() {
        this.completada = true;
    }

    public void añadirEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
    }

    public TarjetaId getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isCompletada() { return completada; }
}
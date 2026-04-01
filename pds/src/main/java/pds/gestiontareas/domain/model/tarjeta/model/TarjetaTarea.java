package pds.gestiontareas.domain.model.tarjeta.model;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;

public class TarjetaTarea extends Tarjeta {
    
	public TarjetaTarea(String titulo, String descripcion) {
        super(titulo, descripcion);
    }
	
	public TarjetaTarea(TarjetaId id, String titulo, String descripcion) {
        super(titulo, descripcion);
        this.id = id;
    }
}
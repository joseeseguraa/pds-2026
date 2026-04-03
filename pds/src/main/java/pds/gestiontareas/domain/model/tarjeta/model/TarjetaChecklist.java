package pds.gestiontareas.domain.model.tarjeta.model;

import java.util.ArrayList;
import java.util.List;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;

public class TarjetaChecklist extends Tarjeta {
    
    private List<ItemChecklist> checklist = new ArrayList<>();

    public TarjetaChecklist(String titulo, String descripcion) {
        super(titulo, descripcion);
    }
    
    public TarjetaChecklist(TarjetaId id, String titulo, String descripcion) {
        super(titulo, descripcion);
        this.id = id;
    }

    public List<ItemChecklist> getChecklist() { return checklist; }
    public void setChecklist(List<ItemChecklist> checklist) { this.checklist = checklist; }
}
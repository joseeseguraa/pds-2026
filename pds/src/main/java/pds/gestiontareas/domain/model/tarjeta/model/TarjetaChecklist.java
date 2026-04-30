package pds.gestiontareas.domain.model.tarjeta.model;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import java.util.ArrayList;
import java.util.List;

public class TarjetaChecklist extends Tarjeta {

    private List<ItemChecklist> checklist;

    // Constructor estándar
    public TarjetaChecklist(String titulo, String descripcion) {
        super(titulo, descripcion);
        this.checklist = new ArrayList<>();
    }

    // Constructor de reconstrucción para la infraestructura (JPA)
    public TarjetaChecklist(TarjetaId id, String titulo, String descripcion) {
        super();
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
        this.etiquetas = new ArrayList<>();
        this.historialVisitas = new ArrayList<>();
        this.checklist = new ArrayList<>();
    }

    protected TarjetaChecklist() {
        super();
    }

    // --- OVERRIDES DE LÓGICA DE CHECKLIST ---

    @Override
    public void añadirItemChecklist(String texto) {
        boolean existe = checklist.stream().anyMatch(item -> item.getTexto().equals(texto));
        if (!existe) {
            this.checklist.add(new ItemChecklist(texto, false));
        }
    }

    @Override
    public void actualizarEstadoItemChecklist(String textoItem, boolean estaCompletado) {
        for (ItemChecklist item : this.checklist) {
            if (item.getTexto().equals(textoItem)) {
                item.setCompletado(estaCompletado);
                break;
            }
        }
    }

    @Override
    public void eliminarItemChecklist(String textoItem) {
        this.checklist.removeIf(item -> item.getTexto().equals(textoItem));
    }

    public List<ItemChecklist> getChecklist() {
        return new ArrayList<>(checklist);
    }
}
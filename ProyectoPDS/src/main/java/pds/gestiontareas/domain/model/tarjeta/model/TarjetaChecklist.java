package pds.gestiontareas.domain.model.tarjeta.model;

import java.util.ArrayList;
import java.util.List;

public class TarjetaChecklist extends Tarjeta {
    
    public static class ItemChecklist {
        private String texto;
        private boolean hecho;

        public ItemChecklist(String texto) {
            this.texto = texto;
            this.hecho = false;
        }
        public void marcarHecho() { this.hecho = true; }
        public String getTexto() { return texto; }
        public boolean isHecho() { return hecho; }
    }

    private List<ItemChecklist> items;

    public TarjetaChecklist(String titulo, String descripcion) {
        super(titulo, descripcion);
        this.items = new ArrayList<>();
    }

    public void añadirItem(String texto) {
        this.items.add(new ItemChecklist(texto));
    }

    public List<ItemChecklist> getItems() { return items; }
}
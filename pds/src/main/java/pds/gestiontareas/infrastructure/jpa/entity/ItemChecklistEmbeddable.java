package pds.gestiontareas.infrastructure.jpa.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ItemChecklistEmbeddable {

    private String texto;
    private boolean completado;

    public ItemChecklistEmbeddable() {}

    public ItemChecklistEmbeddable(String texto, boolean completado) {
        this.texto = texto;
        this.completado = completado;
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
}
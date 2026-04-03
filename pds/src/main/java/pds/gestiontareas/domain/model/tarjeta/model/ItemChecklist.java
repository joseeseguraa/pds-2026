package pds.gestiontareas.domain.model.tarjeta.model;

public class ItemChecklist {
    private String texto;
    private boolean completado;

    public ItemChecklist(String texto, boolean completado) {
        this.texto = texto;
        this.completado = completado;
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    
    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
    
    public void alternar() { this.completado = !this.completado; }
}
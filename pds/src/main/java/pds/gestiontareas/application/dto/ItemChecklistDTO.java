package pds.gestiontareas.application.dto;

public class ItemChecklistDTO {
    private String texto;
    private boolean completado;

    public ItemChecklistDTO(String texto, boolean completado) {
        this.texto = texto;
        this.completado = completado;
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
}
package pds.gestiontareas.application.dto.plantilla;

public class TarjetaPlantillaDTO {
	private String titulo;
    private String tipo; // "TAREA" o "CHECKLIST"

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}

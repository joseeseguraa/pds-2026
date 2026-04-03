package pds.gestiontareas.common.events;

public class TableroModificadoEvent extends EventoDominio {
    private final String tableroId;
    private final String descripcionAccion;

    public TableroModificadoEvent(String tableroId, String descripcionAccion) {
        this.tableroId = tableroId;
        this.descripcionAccion = descripcionAccion;
    }

    public String getTableroId() { return tableroId; }
    public String getDescripcionAccion() { return descripcionAccion; }
}
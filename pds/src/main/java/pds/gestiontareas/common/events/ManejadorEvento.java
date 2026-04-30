package pds.gestiontareas.common.events;

public interface ManejadorEvento<T extends EventoDominio> {
    void manejar(T evento);
}
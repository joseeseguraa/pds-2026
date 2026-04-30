package pds.gestiontareas.common.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AgregadoConEventos {
    
    // transient evita que JPA o serializadores intenten guardar esto en la BD
    private transient List<EventoDominio> domainEvents = new ArrayList<>();

    protected void registrarEvento(EventoDominio evento) {
        this.domainEvents.add(evento);
    }

    public List<EventoDominio> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void limpiarEventos() {
        this.domainEvents.clear();
    }
}
package pds.gestiontareas.common.events;

import java.time.LocalDateTime;

public abstract class EventoDominio {
    private final LocalDateTime ocurridoEl;

    public EventoDominio() {
        this.ocurridoEl = LocalDateTime.now();
    }

    public LocalDateTime getOcurridoEl() {
        return ocurridoEl;
    }
}
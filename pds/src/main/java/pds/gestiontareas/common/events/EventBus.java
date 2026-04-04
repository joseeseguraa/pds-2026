package pds.gestiontareas.common.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Implementación simple de un Bus de Eventos (Pattern Observer/Pub-Sub).
 * Permite desacoplar los componentes de la aplicación.
 */

public class EventBus {

    private static EventBus instance;
    private final Map<Class<? extends EventoDominio>, List<Consumer<? extends EventoDominio>>> suscriptores;

    private EventBus() {
        this.suscriptores = new HashMap<>();
    }

    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public <T extends EventoDominio> void suscribir(Class<T> tipoEvento, Consumer<T> actuador) {
        suscriptores.computeIfAbsent(tipoEvento, k -> new ArrayList<>()).add((Consumer<? extends EventoDominio>) actuador);
    }

    @SuppressWarnings("unchecked")
    public <T extends EventoDominio> void publicar(T evento) {
        List<Consumer<? extends EventoDominio>> lista = suscriptores.get(evento.getClass());
        if (lista != null) {
            lista.forEach(consumidor -> ((Consumer<T>) consumidor).accept(evento));
        }
    }
}
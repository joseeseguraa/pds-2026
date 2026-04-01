package pds.gestiontareas.domain.model.tablero;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pds.gestiontareas.domain.model.tablero.model.ListaTareas;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.usuario.model.Email;

class TableroTest {

    private Tablero tablero;
    private Email emailCreador;

    @BeforeEach
    void setUp() {
        emailCreador = new Email("estudiante@um.es");
        tablero = new Tablero("Proyecto PDS", emailCreador);
    }

    @Test
    void testCreacionTableroYListaCompletadas() {
        assertFalse(tablero.isBloqueado(), "El tablero debería crearse desbloqueado");
        
        boolean tieneListaCompletadas = tablero.getListas().stream()
                .anyMatch(lista -> lista.getTitulo().equals("Completadas"));
        assertTrue(tieneListaCompletadas, "El tablero debe tener una lista de 'Completadas' al crearse");
    }

    @Test
    void testBloquearTableroImpideAñadirTarjetas() {

        tablero.añadirLista("TODO");
        
        ListaTareas listaTodo = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals("TODO"))
                .findFirst().get();

        tablero.bloquear();
        assertTrue(tablero.isBloqueado());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            tablero.añadirTarjetaALista("tarjeta-123", listaTodo.getId());
        });

        assertNotNull(exception.getMessage());
    }

}
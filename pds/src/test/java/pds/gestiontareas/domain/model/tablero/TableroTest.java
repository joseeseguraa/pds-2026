package pds.gestiontareas.domain.model.tablero;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pds.gestiontareas.domain.model.tablero.model.ListaTareas;
import pds.gestiontareas.domain.model.tablero.model.Tablero;

class TableroTest {

    private Tablero tablero;
    private String emailCreador;

    @BeforeEach
    void setUp() {
        emailCreador = "estudiante@um.es";
        tablero = new Tablero("Proyecto PDS", emailCreador);
    }

    @Test
    void testCreacionTableroYListasVacias() {
        assertFalse(tablero.isBloqueado(), "El tablero debería crearse desbloqueado");
        
        assertTrue(tablero.getListas().isEmpty(), "El tablero debe nacer sin listas por defecto");
    }

    @Test
    void testBloquearTableroImpideAñadirTarjetas() {
        tablero.añadirLista("TODO");
        
        ListaTareas listaTodo = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals("TODO"))
                .findFirst()
                .orElseThrow();

        tablero.bloquear();
        assertTrue(tablero.isBloqueado());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            tablero.añadirTarjetaALista("tarjeta-123", listaTodo.getId());
        });

        assertEquals("No se pueden añadir tarjetas, el tablero está bloqueado temporalmente.", exception.getMessage());
    }

    @Test
    void testMoverTarjetaEntreListas() {
        tablero.añadirLista("Origen");
        tablero.añadirLista("Destino");
        
        String idO = tablero.getListas().get(0).getId();
        String idD = tablero.getListas().get(1).getId();
        String tarjetaId = "T-1";
    
        tablero.añadirTarjetaALista(tarjetaId, idO);
        
        tablero.moverTarjeta(tarjetaId, idO, idD);
    
        assertFalse(tablero.getListas().get(0).getTarjetasIds().contains(tarjetaId));
        assertTrue(tablero.getListas().get(1).getTarjetasIds().contains(tarjetaId));
    }
}

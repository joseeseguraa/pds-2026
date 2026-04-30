package pds.gestiontareas.domain.model.tablero;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pds.gestiontareas.application.TableroService;
import pds.gestiontareas.domain.model.tablero.id.ListaTareasId;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaTarea;
import pds.gestiontareas.domain.model.tarjeta.repository.TarjetaRepository;

public class TableroServiceTest {

    @Mock
    private TableroRepository tableroRepository;

    @Mock
    private TarjetaRepository tarjetaRepository;

    @InjectMocks
    private TableroService tableroService;

    private Tablero tableroPrueba;
    private Tarjeta tarjetaPrueba;
    private TableroId tableroId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        tableroId = new TableroId("tablero-123");
        // CORRECCIÓN 1: El constructor ahora requiere la lista de usuarios compartidos al final
        tableroPrueba = new Tablero(tableroId, "Tablero Test", "dueño@test.com", false, new ArrayList<>(), new ArrayList<>());
        tableroPrueba.añadirLista("Por Hacer");
        
        tarjetaPrueba = new TarjetaTarea("Hacer tests", "Aprender a testear");
    }

    @Test
    void crearTarjetaEnLista_DebeCrearTarjetaYGuardarEnTablero() {
        when(tableroRepository.buscarPorId(tableroId)).thenReturn(Optional.of(tableroPrueba));

        TarjetaId nuevaId = tableroService.crearTarjetaEnLista(tableroId, "Por Hacer", "Nueva Tarea", "TAREA");

        assertNotNull(nuevaId, "El ID de la tarjeta no debería ser nulo");
        
        verify(tarjetaRepository, times(1)).guardar(any(Tarjeta.class));
        verify(tableroRepository, atLeastOnce()).guardar(tableroPrueba); 
        
        assertFalse(tableroPrueba.getHistorial().isEmpty(), "El historial debería tener un registro");
    }

    @Test
    void completarTarjetaYBuscarDestino_DebeCrearListaCompletadasSiNoExiste() {
        // CORRECCIÓN 2: El ID devuelto es de tipo ListaTareasId, no String
        ListaTareasId idListaPorHacer = tableroPrueba.getListas().get(0).getId();
        tableroPrueba.añadirTarjetaALista(tarjetaPrueba.getId().getValor(), idListaPorHacer);
        
        when(tableroRepository.buscarPorId(tableroId)).thenReturn(Optional.of(tableroPrueba));
        when(tarjetaRepository.buscarPorId(any(TarjetaId.class))).thenReturn(Optional.of(tarjetaPrueba));

        tableroService.completarTarjetaYBuscarDestino(tableroId, tarjetaPrueba.getId().getValor(), "Por Hacer");

        assertTrue(tarjetaPrueba.isCompletada(), "La tarjeta debería estar marcada como completada");
        
        boolean existeCompletadas = tableroPrueba.getListas().stream()
                .anyMatch(l -> l.getTitulo().equals("Completadas"));
        assertTrue(existeCompletadas, "El servicio debió crear la lista 'Completadas'");
        
        verify(tableroRepository, atLeastOnce()).guardar(tableroPrueba);
    }

    @Test
    void eliminarTarjetaCompletamente_DebeEliminarDeListaYDeRepositorio() {
        // CORRECCIÓN 3: El ID devuelto es de tipo ListaTareasId, no String
        ListaTareasId idListaPorHacer = tableroPrueba.getListas().get(0).getId();
        tableroPrueba.añadirTarjetaALista(tarjetaPrueba.getId().getValor(), idListaPorHacer);
        
        when(tableroRepository.buscarPorId(tableroId)).thenReturn(Optional.of(tableroPrueba));

        tableroService.eliminarTarjetaCompletamente(tableroId, "Por Hacer", tarjetaPrueba.getId().getValor(), "Hacer tests");

        boolean tarjetaEnLista = tableroPrueba.getListas().get(0).getTarjetasIds().contains(tarjetaPrueba.getId().getValor());
        assertFalse(tarjetaEnLista, "La tarjeta debió ser eliminada de la lista");
        
        verify(tarjetaRepository, times(1)).eliminar(any(TarjetaId.class));
    }
}
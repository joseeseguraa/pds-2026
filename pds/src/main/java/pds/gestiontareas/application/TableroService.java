package pds.gestiontareas.application;

import org.springframework.stereotype.Service;

import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;
import pds.gestiontareas.domain.model.usuario.model.Email;

@Service
public class TableroService {

	private final TableroRepository tableroRepository;

    public TableroService(TableroRepository tableroRepository) {
        this.tableroRepository = tableroRepository;
    }
    
    public TableroId crearTablero(String nombreTablero, String emailCreador) {
        Email creador = new Email(emailCreador);
        Tablero nuevoTablero = new Tablero(nombreTablero, creador);
        
        tableroRepository.guardar(nuevoTablero);
        
        return nuevoTablero.getId();
    }

    public void añadirListaATablero(TableroId tableroId, String tituloLista) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        tablero.añadirLista(tituloLista);
        
        tableroRepository.guardar(tablero); 
    }

    public void bloquearTablero(TableroId tableroId) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        tablero.bloquear();
        tableroRepository.guardar(tablero);
    }
    
    public void añadirTarjetaAListaPorNombre(TableroId tableroId, String nombreLista, String tarjetaId) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        String listaId = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreLista))
                .findFirst()
                .map(l -> l.getId())
                .orElseThrow(() -> new IllegalArgumentException("La lista no existe"));

        tablero.añadirTarjetaALista(tarjetaId, listaId);
        tableroRepository.guardar(tablero);
    }
    
    public void moverTarjetaACompletadas(TableroId tableroId, String tarjetaId, String nombreListaOrigen) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));

        String listaOrigenId = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaOrigen))
                .findFirst()
                .map(l -> l.getId())
                .orElseThrow(() -> new IllegalArgumentException("La lista de origen no existe"));

        tablero.moverTarjetaACompletadas(tarjetaId, listaOrigenId);
        
        tableroRepository.guardar(tablero);
    }
}
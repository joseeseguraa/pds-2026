package pds.gestiontareas.application;

import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;
import pds.gestiontareas.domain.model.usuario.model.Email;

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
}
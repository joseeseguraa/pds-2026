package pds.gestiontareas.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.ListaTareas;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.model.TrazaAccion;
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
    
    
    public void moverTarjeta(TableroId tableroId, String tarjetaId, String nombreListaOrigen, String nombreListaDestino) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));

        String listaOrigenId = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaOrigen)).findFirst().map(l -> l.getId())
                .orElseThrow(() -> new IllegalArgumentException("La lista de origen no existe"));

        String listaDestinoId = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaDestino)).findFirst().map(l -> l.getId())
                .orElseThrow(() -> new IllegalArgumentException("La lista de destino no existe"));

        tablero.moverTarjeta(tarjetaId, listaOrigenId, listaDestinoId);
        tableroRepository.guardar(tablero);
    }
    
    public List<Tablero> obtenerTodos() {
        return tableroRepository.buscarTodos();
    }

    public Tablero obtenerTablero(TableroId tableroId) {
        return tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
    }


    public List<String> obtenerNombresListas(TableroId tableroId) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        return tablero.getListas().stream()
                .map(l -> l.getTitulo())
                .collect(Collectors.toList());
    }
    
    public void eliminarTarjetaDeLista(TableroId tableroId, String nombreLista, String tarjetaIdStr) {
        Tablero tablero = obtenerTablero(tableroId);
        
        for (ListaTareas lista : tablero.getListas()) {
            if (lista.getTitulo().equals(nombreLista)) {
                lista.getTarjetasIds().remove(tarjetaIdStr);
                break;
            }
        }
        
        tableroRepository.guardar(tablero);
    }

    public void alternarBloqueo(TableroId tableroId) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        if (tablero.isBloqueado()) {
            tablero.desbloquear();
        } else {
            tablero.bloquear();
        }
        
        tableroRepository.guardar(tablero);
    }

    public List<String> obtenerHistorialTextos(TableroId tableroId) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        return tablero.getHistorial().stream()
                .map(traza -> traza.getDescripcion())
                .collect(Collectors.toList());
    }
    
    public void registrarAccionManual(TableroId tableroId, String mensaje) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        tablero.getHistorial().add(new TrazaAccion(mensaje));
        tableroRepository.guardar(tablero);
    }
    
    public void eliminarLista(TableroId tableroId, String nombreLista) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        tablero.getListas().removeIf(lista -> lista.getTitulo().equals(nombreLista));
        tableroRepository.guardar(tablero);
    }
    
    public List<Tablero> obtenerTablerosPorEmail(String email) {
        return tableroRepository.buscarTodos().stream()
                .filter(t -> t.getCreador() != null && t.getCreador().getDireccion().equalsIgnoreCase(email.trim()))
                .collect(Collectors.toList());
    }
    
}

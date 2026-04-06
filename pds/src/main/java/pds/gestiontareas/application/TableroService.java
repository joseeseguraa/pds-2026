package pds.gestiontareas.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.ListaTareas;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.model.TrazaAccion;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.repository.TarjetaRepository;

@Service
public class TableroService {

    private final TableroRepository tableroRepository;
    private final TarjetaRepository tarjetaRepository;

    public TableroService(TableroRepository tableroRepository, TarjetaRepository tarjetaRepository) {
        this.tableroRepository = tableroRepository;
        this.tarjetaRepository = tarjetaRepository;
    }
    
    public TableroId crearTablero(String nombreTablero, String emailCreador) {
        Tablero nuevoTablero = new Tablero(nombreTablero, emailCreador); 
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
        /*        
        String listaId = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreLista))
                .findFirst()
                .map(l -> l.getId())
                .orElseThrow(() -> new IllegalArgumentException("La lista no existe"));
		*/
        pds.gestiontareas.domain.model.tablero.model.ListaTareas lista = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreLista))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista no existe"));
        
        if (lista.getLimiteTarjetas() != null && lista.getTarjetasIds().size() > lista.getLimiteTarjetas()) {
            throw new IllegalStateException("Límite alcanzado. La lista '" + nombreLista + "' solo permite " + lista.getLimiteTarjetas() + " tareas.");
        }
        
        tablero.añadirTarjetaALista(tarjetaId, lista.getId());
        tableroRepository.guardar(tablero);
    }
    
    public void moverTarjeta(TableroId tableroId, String tarjetaId, String nombreListaOrigen, String nombreListaDestino) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
        /*
        String listaOrigenId = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaOrigen)).findFirst().map(l -> l.getId())
                .orElseThrow(() -> new IllegalArgumentException("La lista de origen no existe"));

        String listaDestinoId = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaDestino)).findFirst().map(l -> l.getId())
                .orElseThrow(() -> new IllegalArgumentException("La lista de destino no existe"));
        */
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(new TarjetaId(tarjetaId))
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe"));
        
        ListaTareas origen = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaOrigen)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista de origen no existe"));

        ListaTareas destino = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaDestino)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista de destino no existe"));
        
        if (destino.getLimiteTarjetas() != null && destino.getTarjetasIds().size() > destino.getLimiteTarjetas()) {
            throw new IllegalStateException("No se puede mover. La lista '" + nombreListaDestino + "' tiene un límite de " + destino.getLimiteTarjetas() + " tareas.");
        }
        
        for (String listaRequerida : destino.getListasPrecedentesRequeridas()) {
            if (!nombreListaOrigen.equals(listaRequerida) && !tarjeta.haVisitado(listaRequerida)) {
                throw new IllegalStateException("Flujo inválido. Para entrar a '" + nombreListaDestino + "', la tarea debe haber pasado antes por '" + listaRequerida + "'.");
            }
        }
        
        //tablero.moverTarjeta(tarjetaId, listaOrigenId, listaDestinoId);
        tablero.moverTarjeta(tarjetaId, origen.getId(), destino.getId());
        tarjeta.registrarVisita(nombreListaDestino);
        tableroRepository.guardar(tablero);
        tarjetaRepository.guardar(tarjeta);
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
        
        tablero.eliminarTarjetaDeLista(nombreLista, tarjetaIdStr);
        
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
                .map(TrazaAccion::getDescripcion)
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
                
        tablero.eliminarLista(nombreLista);
        
        tableroRepository.guardar(tablero);
    }
    
    public List<Tablero> obtenerTablerosPorEmail(String email) {
        return tableroRepository.buscarTodos().stream()
                .filter(t -> t.getCreador() != null && t.getCreador().equalsIgnoreCase(email.trim()))
                .collect(Collectors.toList());
    }
    
    public void limpiarHistorial(TableroId tableroId) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));

        tablero.getHistorial().clear();
        tablero.getHistorial().add(new TrazaAccion("Se vació el historial de acciones manualmente."));       
        tableroRepository.guardar(tablero);
    }
    
    public void actualizarReglasLista(TableroId tableroId, String nombreLista, Integer limite, String listaRequerida) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        pds.gestiontareas.domain.model.tablero.model.ListaTareas lista = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreLista))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista no existe"));
                
        lista.setLimiteTarjetas(limite);
        lista.getListasPrecedentesRequeridas().clear();
        
        if (listaRequerida != null) {
            lista.añadirListaRequerida(listaRequerida);
        }
        
        tableroRepository.guardar(tablero);
    }
}
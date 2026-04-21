package pds.gestiontareas.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Transactional
    public TableroId crearTablero(String nombreTablero, String emailCreador) {
        Tablero nuevoTablero = new Tablero(nombreTablero, emailCreador); 
        tableroRepository.guardar(nuevoTablero);
        return nuevoTablero.getId();
    }

    @Transactional
    public void añadirListaATablero(TableroId tableroId, String tituloLista) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        tablero.añadirLista(tituloLista);
        tableroRepository.guardar(tablero); 
    }
    
    
    @Transactional
    public TarjetaId crearTarjetaEnLista(TableroId tableroId, String nombreLista, String tituloTarjeta, String tipo) {
        Tarjeta nuevaTarjeta;
        if ("CHECKLIST".equals(tipo)) {
            nuevaTarjeta = new pds.gestiontareas.domain.model.tarjeta.model.TarjetaChecklist(tituloTarjeta, "");
        } else {
            nuevaTarjeta = new pds.gestiontareas.domain.model.tarjeta.model.TarjetaTarea(tituloTarjeta, "");
        }
        tarjetaRepository.guardar(nuevaTarjeta);
        TarjetaId nuevaId = nuevaTarjeta.getId();
        añadirTarjetaAListaPorNombre(tableroId, nombreLista, nuevaId.getValor());
        registrarAccionManual(tableroId, "Se creó la tarjeta '" + tituloTarjeta + "' de tipo " + tipo + " en la lista '" + nombreLista + "'.");
        
        return nuevaId;
    }

    @Transactional
    public void bloquearTablero(TableroId tableroId) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        tablero.bloquear();
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
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
        
        lista.validarReglasEntrada(null, null);
        
        tablero.añadirTarjetaALista(tarjetaId, lista.getId());
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
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
        
        destino.validarReglasEntrada(tarjeta, nombreListaOrigen);
        
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
    
    @Transactional
    public void eliminarTarjetaDeLista(TableroId tableroId, String nombreLista, String tarjetaIdStr) {
        Tablero tablero = obtenerTablero(tableroId);
        
        tablero.eliminarTarjetaDeLista(nombreLista, tarjetaIdStr);
        
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
    public void eliminarTarjetaCompletamente(TableroId tableroId, String nombreLista, String tarjetaId, String nombreTarjeta) {
        eliminarTarjetaDeLista(tableroId, nombreLista, tarjetaId);
        tarjetaRepository.eliminar(new TarjetaId(tarjetaId));
        registrarAccionManual(tableroId, "Se eliminó la tarjeta '" + nombreTarjeta + "'.");
    }

    @Transactional
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
    
    @Transactional
    public void registrarAccionManual(TableroId tableroId, String mensaje) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        tablero.getHistorial().add(new TrazaAccion(mensaje));
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
    public void eliminarLista(TableroId tableroId, String nombreLista) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        tablero.eliminarLista(nombreLista);
        tablero.getHistorial().add(new TrazaAccion("Se eliminó la lista completa '" + nombreLista + "'."));
        tableroRepository.guardar(tablero);
    }
    
    public List<Tablero> obtenerTablerosPorEmail(String email) {
        return tableroRepository.buscarTodos().stream()
                .filter(t -> t.getCreador() != null && t.getCreador().equalsIgnoreCase(email.trim()))
                .collect(Collectors.toList());
    }
    
    @Transactional
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
    
    @Transactional
    public void completarTarjetaYBuscarDestino(TableroId tableroId, String tarjetaId, String nombreListaOrigen) {
        Tablero tablero = tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
                
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(new TarjetaId(tarjetaId))
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe"));

        tarjeta.setCompletada(true);

        boolean existeCompletadas = tablero.getListas().stream()
                .anyMatch(l -> l.getTitulo().equals("Completadas"));

        if (!existeCompletadas) {
            tablero.añadirLista("Completadas");
        }

        if (!nombreListaOrigen.equals("Completadas")) {
            ListaTareas origen = tablero.getListas().stream()
                    .filter(l -> l.getTitulo().equals(nombreListaOrigen)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("La lista de origen no existe"));

            ListaTareas destino = tablero.getListas().stream()
                    .filter(l -> l.getTitulo().equals("Completadas")).findFirst()
                    .orElseThrow();
                    
            tablero.moverTarjeta(tarjetaId, origen.getId(), destino.getId());
            tarjeta.registrarVisita("Completadas");
        }

        tablero.getHistorial().add(new TrazaAccion("Se marcó como completada la tarjeta '" + tarjeta.getTitulo() + "' y se movió a Completadas."));
        
        tableroRepository.guardar(tablero);
        tarjetaRepository.guardar(tarjeta);
    }
}
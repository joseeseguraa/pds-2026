package pds.gestiontareas.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pds.gestiontareas.application.dto.ItemChecklistDTO;
import pds.gestiontareas.application.dto.ListaDTO;
import pds.gestiontareas.application.dto.TableroDTO;
import pds.gestiontareas.application.dto.TarjetaDTO;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.ListaTareas;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.model.TrazaAccion;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Etiqueta;
import pds.gestiontareas.domain.model.tarjeta.model.PermisoAcceso;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaChecklist;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaTarea;
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

    public Tablero obtenerTablero(TableroId tableroId) {
        return tableroRepository.buscarPorId(tableroId)
                .orElseThrow(() -> new IllegalArgumentException("El tablero no existe"));
    }

    // Devuelve el TableroDTO y asegura que todas sus tarjetas estén mapeadas al 100%
    public TableroDTO obtenerDatosTablero(TableroId tableroId) {
        Tablero tablero = obtenerTablero(tableroId);
        TableroDTO dto = new TableroDTO();
        
        dto.setId(tablero.getId().getValor());
        dto.setNombre(tablero.getNombre());
        dto.setBloqueado(tablero.isBloqueado());
        
        List<ListaDTO> listasDto = new ArrayList<>();
        
        for (ListaTareas lista : tablero.getListas()) {
            ListaDTO lDto = new ListaDTO();
            lDto.setNombre(lista.getTitulo());
            lDto.setLimite(lista.getLimiteTarjetas());
            
            List<TarjetaDTO> tarjetasDto = new ArrayList<>();
            for (String tid : lista.getTarjetasIds()) {
                tarjetaRepository.buscarPorId(new TarjetaId(tid)).ifPresent(tarjeta -> {
                    tarjetasDto.add(mapearATarjetaDTO(tarjeta));
                });
            }
            lDto.setTarjetas(tarjetasDto);
            listasDto.add(lDto);
        }
        
        dto.setListas(listasDto);
        return dto;
    }

    // Método auxiliar de mapeo (Idéntico al de TarjetaService para no acoplar los servicios)
    private TarjetaDTO mapearATarjetaDTO(Tarjeta tarjeta) {
        TarjetaDTO dto = new TarjetaDTO();
        dto.setId(tarjeta.getId().getValor());
        dto.setTitulo(tarjeta.getTitulo());
        dto.setDescripcion(tarjeta.getDescripcion());
        dto.setCompletada(tarjeta.isCompletada());
        
        dto.setColoresEtiquetas(tarjeta.getEtiquetas().stream()
                .map(Etiqueta::getColorHex)
                .collect(Collectors.toList()));
        
        if (tarjeta instanceof TarjetaChecklist) {
            dto.setEsChecklist(true);
            dto.setItemsChecklist(((TarjetaChecklist) tarjeta).getChecklist().stream()
                    .map(item -> new ItemChecklistDTO(item.getTexto(), item.isCompletado()))
                    .collect(Collectors.toList()));
        } else {
            dto.setEsChecklist(false);
            dto.setItemsChecklist(new ArrayList<>());
        }
        return dto;
    }

    public List<Tablero> obtenerTodos() {
        return tableroRepository.buscarTodos();
    }

    @Transactional
    public void añadirListaATablero(TableroId tableroId, String tituloLista) {
        Tablero tablero = obtenerTablero(tableroId);
        tablero.añadirLista(tituloLista);
        tableroRepository.guardar(tablero); 
    }
    
    @Transactional
    public TarjetaId crearTarjetaEnLista(TableroId tableroId, String nombreLista, String tituloTarjeta, String tipo) {
        Tarjeta nuevaTarjeta;
        if ("CHECKLIST".equals(tipo)) {
            nuevaTarjeta = new TarjetaChecklist(tituloTarjeta, "");
        } else {
            nuevaTarjeta = new TarjetaTarea(tituloTarjeta, "");
        }
        tarjetaRepository.guardar(nuevaTarjeta);
        TarjetaId nuevaId = nuevaTarjeta.getId();
        añadirTarjetaAListaPorNombre(tableroId, nombreLista, nuevaId.getValor());
        registrarAccionManual(tableroId, "Se creó la tarjeta '" + tituloTarjeta + "' de tipo " + tipo + " en la lista '" + nombreLista + "'.");
        
        return nuevaId;
    }

    @Transactional
    public void bloquearTablero(TableroId tableroId) {
        Tablero tablero = obtenerTablero(tableroId);
        tablero.bloquear();
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
    public void añadirTarjetaAListaPorNombre(TableroId tableroId, String nombreLista, String tarjetaId) {
        Tablero tablero = obtenerTablero(tableroId);
        
        ListaTareas lista = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreLista))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista no existe"));
        
        lista.validarReglasEntrada(null, null);
        
        tablero.añadirTarjetaALista(tarjetaId, lista.getId());
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
    public void moverTarjeta(TableroId tableroId, String tarjetaId, String nombreListaOrigen, String nombreListaDestino) {
        Tablero tablero = obtenerTablero(tableroId);
        
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(new TarjetaId(tarjetaId))
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe"));
        
        ListaTareas origen = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaOrigen)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista de origen no existe"));

        ListaTareas destino = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreListaDestino)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista de destino no existe"));
        
        destino.validarReglasEntrada(tarjeta, nombreListaOrigen);
        
        tablero.moverTarjeta(tarjetaId, origen.getId(), destino.getId());
        tarjeta.registrarVisita(nombreListaDestino);
        
        tableroRepository.guardar(tablero);
        tarjetaRepository.guardar(tarjeta);
    }

    public List<String> obtenerNombresListas(TableroId tableroId) {
        Tablero tablero = obtenerTablero(tableroId);
        return tablero.getListas().stream()
                .map(ListaTareas::getTitulo)
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
        Tablero tablero = obtenerTablero(tableroId);
        if (tablero.isBloqueado()) {
            tablero.desbloquear();
        } else {
            tablero.bloquear();
        }
        tableroRepository.guardar(tablero);
    }

    public List<String> obtenerHistorialTextos(TableroId tableroId) {
        Tablero tablero = obtenerTablero(tableroId);
        return tablero.getHistorial().stream()
                .map(TrazaAccion::getDescripcion)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void registrarAccionManual(TableroId tableroId, String mensaje) {
        Tablero tablero = obtenerTablero(tableroId);
        tablero.registrarAccionEnHistorial(mensaje);
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
    public void eliminarLista(TableroId tableroId, String nombreLista) {
        Tablero tablero = obtenerTablero(tableroId);
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
        Tablero tablero = obtenerTablero(tableroId);
        tablero.limpiarHistorial();
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
    public void actualizarReglasLista(TableroId tableroId, String nombreLista, Integer limite, String listaRequerida) {
        Tablero tablero = obtenerTablero(tableroId);
                
        ListaTareas lista = tablero.getListas().stream()
                .filter(l -> l.getTitulo().equals(nombreLista))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista no existe"));
                
        lista.setLimiteTarjetas(limite);
        lista.limpiarListasRequeridas();
        
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
    
    @Transactional
    public void compartirTablero(TableroId tableroId, String emailDueño, String emailACompartir) {
        Tablero tablero = obtenerTablero(tableroId);
        
        if (!tablero.getCreador().equalsIgnoreCase(emailDueño)) {
            throw new SecurityException("Solo el dueño del tablero puede compartirlo.");
        }
        
        tablero.compartirCon(emailACompartir);
        tableroRepository.guardar(tablero);
    }
    
    @Transactional
    public void asignarPermisoTarjeta(TableroId tableroId, String tarjetaId, String emailDueño, String emailUsuario, String nivelPermiso) {
        Tablero tablero = obtenerTablero(tableroId);
        
        if (!tablero.getCreador().equalsIgnoreCase(emailDueño)) {
            throw new SecurityException("Solo el dueño puede asignar permisos granulares.");
        }
        
        if (!tablero.getUsuariosCompartidos().contains(emailUsuario)) {
            throw new IllegalArgumentException("El usuario debe estar primero invitado al tablero para recibir permisos en sus tarjetas.");
        }
        
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(new TarjetaId(tarjetaId))
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe"));
                
        // El nivelPermiso esperado será "LECTURA" o "ESCRITURA"
        tarjeta.asignarPermiso(emailUsuario, PermisoAcceso.valueOf(nivelPermiso.toUpperCase()));
        
        tarjetaRepository.guardar(tarjeta);
    }
}
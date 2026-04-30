package pds.gestiontareas.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pds.gestiontareas.application.dto.ItemChecklistDTO;
import pds.gestiontareas.application.dto.TarjetaDTO;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Etiqueta;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaChecklist;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaTarea;
import pds.gestiontareas.domain.model.tarjeta.repository.TarjetaRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class TarjetaService {

    private final TarjetaRepository tarjetaRepository;

    public TarjetaService(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }

    @Transactional
    public TarjetaId crearTarjeta(String titulo, String tipo) {
        Tarjeta nuevaTarjeta;
        if ("CHECKLIST".equals(tipo)) {
            nuevaTarjeta = new TarjetaChecklist(titulo, "");
        } else {
            nuevaTarjeta = new TarjetaTarea(titulo, "");
        }
        tarjetaRepository.guardar(nuevaTarjeta);
        return nuevaTarjeta.getId();
    }
    
    public String obtenerTituloTarjeta(String tarjetaIdStr) {
        return tarjetaRepository.buscarPorId(new TarjetaId(tarjetaIdStr))
                .map(Tarjeta::getTitulo)
                .orElse("Tarea desconocida");
    }
    
    // Método interno que devuelve la ENTIDAD (usado por la lógica de negocio)
    public Tarjeta obtenerTarjeta(String tarjetaIdStr) {
        return tarjetaRepository.buscarPorId(new TarjetaId(tarjetaIdStr))
                .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada: " + tarjetaIdStr));
    }

    // Devuelve un DTO completo (usado por la UI - JavaFX)
    public TarjetaDTO obtenerDatosTarjeta(String tarjetaIdStr) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaIdStr);
        return mapearATarjetaDTO(tarjeta);
    }

    // Método auxiliar para asegurar un mapeo perfecto de Entidad a DTO
    public TarjetaDTO mapearATarjetaDTO(Tarjeta tarjeta) {
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

    @Transactional
    public void actualizarDescripcion(String tarjetaId, String nuevaDescripcion) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaId);
        tarjeta.cambiarDescripcion(nuevaDescripcion);
        tarjetaRepository.guardar(tarjeta);
    }
    
    @Transactional
    public void añadirEtiqueta(String tarjetaId, String nombre, String colorHex) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaId);
        if (!tarjeta.tieneEtiqueta(colorHex)) {
            tarjeta.añadirEtiqueta(new Etiqueta(nombre, colorHex));
            tarjetaRepository.guardar(tarjeta);
        }
    }
    
    @Transactional
    public void quitarEtiqueta(String tarjetaId, String colorHex) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaId);
        tarjeta.quitarEtiqueta(colorHex);
        tarjetaRepository.guardar(tarjeta);
    }
    
    @Transactional
    public void eliminarTarjeta(String tarjetaIdStr) {
        tarjetaRepository.eliminar(new TarjetaId(tarjetaIdStr));
    }

    @Transactional
    public void añadirItemChecklist(String tarjetaIdStr, String texto) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaIdStr);
        tarjeta.añadirItemChecklist(texto);
        tarjetaRepository.guardar(tarjeta);
    }

    @Transactional
    public void alternarEstadoChecklist(String tarjetaIdStr, String textoItem, boolean estaCompletado) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaIdStr);
        tarjeta.actualizarEstadoItemChecklist(textoItem, estaCompletado);
        tarjetaRepository.guardar(tarjeta);
    }
    
    @Transactional
    public void eliminarItemChecklist(String tarjetaIdStr, String textoItem) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaIdStr);
        tarjeta.eliminarItemChecklist(textoItem);
        tarjetaRepository.guardar(tarjeta);
    }
    
    @Transactional
    public void cambiarEstadoCompletada(String tarjetaIdStr, boolean estado) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaIdStr);
        tarjeta.setCompletada(estado);
        tarjetaRepository.guardar(tarjeta);
    }
}
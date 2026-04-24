package pds.gestiontareas.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Etiqueta;
import pds.gestiontareas.domain.model.tarjeta.model.ItemChecklist;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaChecklist;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaTarea;
import pds.gestiontareas.domain.model.tarjeta.repository.TarjetaRepository;

@Service
public class TarjetaService {

    private final TarjetaRepository tarjetaRepository;

    public TarjetaService(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }

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
                .map(t -> t.getTitulo())
                .orElse("Tarea desconocida");
    }
    
    public Tarjeta obtenerTarjeta(String tarjetaId) {
        return tarjetaRepository.buscarPorId(new TarjetaId(tarjetaId))
                .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada"));
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
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(new TarjetaId(tarjetaIdStr))
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe"));
                
        if (tarjeta instanceof TarjetaChecklist) {
            ((TarjetaChecklist) tarjeta).getChecklist().add(new ItemChecklist(texto, false));
            tarjetaRepository.guardar(tarjeta);
        }
    }

    @Transactional
    public void alternarEstadoChecklist(String tarjetaIdStr, String textoItem, boolean estaCompletado) {
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(new TarjetaId(tarjetaIdStr))
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe"));
                
        for (ItemChecklist item : ((TarjetaChecklist) tarjeta).getChecklist()) {
            if (item.getTexto().equals(textoItem)) {
                item.setCompletado(estaCompletado);
                break;
            }
        }
        tarjetaRepository.guardar(tarjeta);
    }
    
    @Transactional
    public void eliminarItemChecklist(String tarjetaIdStr, String textoItem) {
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(new TarjetaId(tarjetaIdStr))
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe"));
                
        ((TarjetaChecklist) tarjeta).getChecklist().removeIf(item -> item.getTexto().equals(textoItem));
        tarjetaRepository.guardar(tarjeta);
    }
    
    @Transactional
    public void cambiarEstadoCompletada(String tarjetaIdStr, boolean estado) {
        Tarjeta tarjeta = tarjetaRepository.buscarPorId(new TarjetaId(tarjetaIdStr))
                .orElseThrow(() -> new IllegalArgumentException("La tarjeta no existe"));
                
        tarjeta.setCompletada(estado);
        tarjetaRepository.guardar(tarjeta);
    }
    
}
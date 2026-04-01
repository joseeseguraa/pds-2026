package pds.gestiontareas.application;

import org.springframework.stereotype.Service;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaTarea;
import pds.gestiontareas.domain.model.tarjeta.repository.TarjetaRepository;

@Service
public class TarjetaService {

    private final TarjetaRepository tarjetaRepository;

    public TarjetaService(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }

    public TarjetaId crearTarjeta(String titulo) {
        TarjetaTarea nuevaTarjeta = new TarjetaTarea(titulo, "");
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

    public void actualizarDescripcion(String tarjetaId, String nuevaDescripcion) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaId);
        tarjeta.cambiarDescripcion(nuevaDescripcion);
        tarjetaRepository.guardar(tarjeta);
    }
    
    public void añadirEtiqueta(String tarjetaId, String nombre, String colorHex) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaId);
        
        if (!tarjeta.tieneEtiqueta(colorHex)) {
            tarjeta.añadirEtiqueta(new pds.gestiontareas.domain.model.tarjeta.model.Etiqueta(nombre, colorHex));
            tarjetaRepository.guardar(tarjeta);
        }
    }
    
    public void quitarEtiqueta(String tarjetaId, String colorHex) {
        Tarjeta tarjeta = obtenerTarjeta(tarjetaId);
        tarjeta.quitarEtiqueta(colorHex);
        tarjetaRepository.guardar(tarjeta);
    }
    
    public void eliminarTarjeta(String tarjetaIdStr) {
        tarjetaRepository.eliminar(new TarjetaId(tarjetaIdStr));
    }

}
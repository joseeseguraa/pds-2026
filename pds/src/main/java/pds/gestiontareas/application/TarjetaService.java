package pds.gestiontareas.application;

import org.springframework.stereotype.Service;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
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
}
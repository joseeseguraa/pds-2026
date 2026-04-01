package pds.gestiontareas.domain.model.tarjeta.repository;

import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import java.util.Optional;

public interface TarjetaRepository {
    void guardar(Tarjeta tarjeta);
    Optional<Tarjeta> buscarPorId(TarjetaId id);
}
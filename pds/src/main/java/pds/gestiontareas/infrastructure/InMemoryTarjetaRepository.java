package pds.gestiontareas.infrastructure;

import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.repository.TarjetaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// @Repository
public class InMemoryTarjetaRepository implements TarjetaRepository {
    
    private final Map<TarjetaId, Tarjeta> baseDeDatos = new HashMap<>();

    @Override
    public void guardar(Tarjeta tarjeta) {
        baseDeDatos.put(tarjeta.getId(), tarjeta);
    }

    @Override
    public Optional<Tarjeta> buscarPorId(TarjetaId id) {
        return Optional.ofNullable(baseDeDatos.get(id));
    }

	@Override
	public void eliminar(TarjetaId id) {
		// TODO Auto-generated method stub
		
	}


}
package pds.gestiontareas.infrastructure;

import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTableroRepository implements TableroRepository {
    
    private final Map<TableroId, Tablero> baseDeDatos = new HashMap<>();

    @Override
    public void guardar(Tablero tablero) {
        baseDeDatos.put(tablero.getId(), tablero);
    }

    @Override
    public Optional<Tablero> buscarPorId(TableroId id) {
        return Optional.ofNullable(baseDeDatos.get(id));
    }
}
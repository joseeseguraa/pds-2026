package pds.gestiontareas.domain.model.tablero.repository;


import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import java.util.Optional;

public interface TableroRepository {
    void guardar(Tablero tablero);
    Optional<Tablero> buscarPorId(TableroId id);
}
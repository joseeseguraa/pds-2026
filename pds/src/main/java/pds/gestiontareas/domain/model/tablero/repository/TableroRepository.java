package pds.gestiontareas.domain.model.tablero.repository;

import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.Tablero;

import java.util.List;
import java.util.Optional;

public interface TableroRepository {
    void guardar(Tablero tablero);
    Optional<Tablero> buscarPorId(TableroId id);
    List<Tablero> buscarTodos();
}
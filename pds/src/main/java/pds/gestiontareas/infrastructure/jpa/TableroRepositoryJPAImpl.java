package pds.gestiontareas.infrastructure.jpa;


import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;
import pds.gestiontareas.domain.model.tablero.id.TableroId;

import java.util.Optional;

//@Repository Lo comento temp porque sino tengo conflictos y no me funciona
public class TableroRepositoryJPAImpl implements TableroRepository {

    private final TableroJpaRepository jpaRepository;

    public TableroRepositoryJPAImpl(TableroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void guardar(Tablero tablero) {
        TableroEntity entity = new TableroEntity();
        entity.setId(tablero.getId().getValor());
        entity.setNombre(tablero.getNombre());
        entity.setBloqueado(tablero.isBloqueado());
        
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Tablero> buscarPorId(TableroId id) {
        return Optional.empty();
    }
}
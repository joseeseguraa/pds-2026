package pds.gestiontareas.infrastructure.jpa;

import org.springframework.stereotype.Repository;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;
import pds.gestiontareas.domain.model.tablero.id.TableroId;

import java.util.Optional;

@Repository
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
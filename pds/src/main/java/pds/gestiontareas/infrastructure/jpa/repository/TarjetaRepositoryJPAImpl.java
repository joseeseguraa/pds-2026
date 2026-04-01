package pds.gestiontareas.infrastructure.jpa.repository;

import org.springframework.stereotype.Repository;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaTarea;
import pds.gestiontareas.domain.model.tarjeta.repository.TarjetaRepository;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.infrastructure.jpa.TarjetaJpaRepository;
import pds.gestiontareas.infrastructure.jpa.entity.TarjetaEntity;

import java.util.Optional;

@Repository 
public class TarjetaRepositoryJPAImpl implements TarjetaRepository {

    private final TarjetaJpaRepository jpaRepository;

    public TarjetaRepositoryJPAImpl(TarjetaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void guardar(Tarjeta tarjeta) {
        TarjetaEntity entity = new TarjetaEntity();
        
        entity.setId(tarjeta.getId().getValor());
        entity.setTitulo(tarjeta.getTitulo());
        entity.setDescripcion(tarjeta.getDescripcion());
        
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Tarjeta> buscarPorId(TarjetaId id) {
        return jpaRepository.findById(id.getValor()).map(entity -> 
            new TarjetaTarea(
                new TarjetaId(entity.getId()),
                entity.getTitulo(),
                entity.getDescripcion()
            )
        );
    }
}
package pds.gestiontareas.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import pds.gestiontareas.infrastructure.jpa.entity.TarjetaEntity;

public interface TarjetaJpaRepository extends JpaRepository<TarjetaEntity, String> {
}
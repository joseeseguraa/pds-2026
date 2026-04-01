package pds.gestiontareas.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import pds.gestiontareas.infrastructure.jpa.entity.TableroEntity;

public interface TableroJpaRepository extends JpaRepository<TableroEntity, String> {
}
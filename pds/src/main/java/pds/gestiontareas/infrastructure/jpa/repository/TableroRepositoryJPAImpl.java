package pds.gestiontareas.infrastructure.jpa.repository;

import org.springframework.stereotype.Repository;

import pds.gestiontareas.domain.model.tablero.model.ListaTareas;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tablero.repository.TableroRepository;
import pds.gestiontareas.domain.model.usuario.model.Email;
import pds.gestiontareas.infrastructure.jpa.TableroJpaRepository;
import pds.gestiontareas.infrastructure.jpa.entity.ListaTareasEntity;
import pds.gestiontareas.infrastructure.jpa.entity.TableroEntity;
import pds.gestiontareas.domain.model.tablero.id.TableroId;

import java.util.ArrayList;
import java.util.List;
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
        
        entity.setEmailCreador(tablero.getCreador().getDireccion());
        
        List<ListaTareasEntity> listasEntities = new ArrayList<>();
        
        for (ListaTareas listaDominio : tablero.getListas()) {
            ListaTareasEntity listaEntity = new ListaTareasEntity();
            listaEntity.setId(listaDominio.getId());
            listaEntity.setNombre(listaDominio.getTitulo());
            
            listaEntity.setTarjetasIds(new ArrayList<>(listaDominio.getTarjetasIds()));
            
            listasEntities.add(listaEntity);
        }
        
        entity.setListas(listasEntities);
        
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Tablero> buscarPorId(TableroId id) {
        Optional<TableroEntity> entityOpt = jpaRepository.findById(id.getValor());

        if (entityOpt.isEmpty()) {
            return Optional.empty();
        }

        TableroEntity entity = entityOpt.get();

        List<ListaTareas> listasDominio = new ArrayList<>();
        
        if (entity.getListas() != null) {
            for (ListaTareasEntity listaEntity : entity.getListas()) {
                ListaTareas lista = new ListaTareas(
                    listaEntity.getId(),
                    listaEntity.getNombre(),
                    listaEntity.getTarjetasIds()
                );
                listasDominio.add(lista);
            }
        }

        Email creadorEmail = new Email(entity.getEmailCreador()); 
        TableroId tableroId = new TableroId(entity.getId()); 

        Tablero tableroReconstruido = new Tablero(
            tableroId,
            entity.getNombre(),
            creadorEmail,
            entity.isBloqueado(),
            listasDominio
        );

        return Optional.of(tableroReconstruido);
    }
    
    @Override
    public List<Tablero> buscarTodos() {
        List<Tablero> todos = new ArrayList<>();
        for (TableroEntity entity : jpaRepository.findAll()) {
            buscarPorId(new TableroId(entity.getId())).ifPresent(todos::add);
        }
        return todos;
    }
}
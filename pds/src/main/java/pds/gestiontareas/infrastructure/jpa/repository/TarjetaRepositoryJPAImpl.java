package pds.gestiontareas.infrastructure.jpa.repository;

import org.springframework.stereotype.Repository;

import pds.gestiontareas.domain.model.tarjeta.model.Etiqueta;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaChecklist;
import pds.gestiontareas.domain.model.tarjeta.model.ItemChecklist;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaTarea;
import pds.gestiontareas.domain.model.tarjeta.repository.TarjetaRepository;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.infrastructure.jpa.TarjetaJpaRepository;
import pds.gestiontareas.infrastructure.jpa.entity.EtiquetaEmbeddable;
import pds.gestiontareas.infrastructure.jpa.entity.ItemChecklistEmbeddable;
import pds.gestiontareas.infrastructure.jpa.entity.TarjetaEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        entity.setCompletada(tarjeta.isCompletada());
        
        entity.setTipo(tarjeta instanceof TarjetaChecklist ? "CHECKLIST" : "TAREA");
        
        List<EtiquetaEmbeddable> embeddables = tarjeta.getEtiquetas().stream()
                .map(e -> new EtiquetaEmbeddable(e.getNombre(), e.getColor()))
                .collect(Collectors.toList());
        entity.setEtiquetas(embeddables);
        
        List<ItemChecklistEmbeddable> checklistEntities = new ArrayList<>();
        if (tarjeta instanceof TarjetaChecklist) {
            for (ItemChecklist item : ((TarjetaChecklist) tarjeta).getChecklist()) {
                checklistEntities.add(new ItemChecklistEmbeddable(item.getTexto(), item.isCompletado()));
            }
        }
        entity.setChecklist(checklistEntities);
        
        jpaRepository.save(entity);
    }
    
    @Override
    public Optional<Tarjeta> buscarPorId(TarjetaId id) {
        return jpaRepository.findById(id.getValor()).map(entity -> {
            Tarjeta tarjeta;
            
            if ("CHECKLIST".equals(entity.getTipo())) {
                tarjeta = new TarjetaChecklist(new TarjetaId(entity.getId()), entity.getTitulo(), entity.getDescripcion());
                if (entity.getChecklist() != null) {
                    for (ItemChecklistEmbeddable itemEmb : entity.getChecklist()) {
                        ((TarjetaChecklist) tarjeta).getChecklist().add(new ItemChecklist(itemEmb.getTexto(), itemEmb.isCompletado()));
                    }
                }
            } else {
                tarjeta = new TarjetaTarea(new TarjetaId(entity.getId()), entity.getTitulo(), entity.getDescripcion());
            }
            
            if (entity.isCompletada()) {
                tarjeta.marcarCompletada();
            }
            
            if (entity.getEtiquetas() != null) {
                for (EtiquetaEmbeddable emb : entity.getEtiquetas()) {
                    tarjeta.añadirEtiqueta(new Etiqueta(emb.getNombre(), emb.getColorHex()));
                }
            }
            
            return tarjeta;
        });
    }
    
    @Override
    public void eliminar(TarjetaId id) {
        jpaRepository.deleteById(id.getValor());
    }
    
}
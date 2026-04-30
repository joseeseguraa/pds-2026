package pds.gestiontareas.application;

import pds.gestiontareas.application.dto.plantilla.*;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlantillaService {
    
    private final TableroService tableroService;
    private final TarjetaService tarjetaService;

    public PlantillaService(TableroService tableroService, TarjetaService tarjetaService) {
        this.tableroService = tableroService;
        this.tarjetaService = tarjetaService;
    }

    @Transactional
    public TableroId crearTableroDesdePlantilla(TableroPlantillaDTO plantilla, String emailCreador) {
        // 1. Crear el Tablero base
        TableroId tableroId = tableroService.crearTablero(plantilla.getNombre(), emailCreador);

        // 2. Crear las listas
        if (plantilla.getListas() != null) {
            for (ListaPlantillaDTO listaDTO : plantilla.getListas()) {
                
                tableroService.añadirListaATablero(tableroId, listaDTO.getNombre());               	

                // Si la plantilla define un límite, se lo aplicamos
                if (listaDTO.getLimite() != null) {
                    tableroService.actualizarReglasLista(tableroId, listaDTO.getNombre(), listaDTO.getLimite(), null);
                }

                // 3. Crear las tarjetas dentro de esta lista
                if (listaDTO.getTarjetas() != null) {
                    for (TarjetaPlantillaDTO tarjetaDTO : listaDTO.getTarjetas()) {
                        String tipo = tarjetaDTO.getTipo() != null ? tarjetaDTO.getTipo() : "TAREA";                            
                        
                        // Creamos la tarjeta
                        TarjetaId nuevaTarjetaId = tarjetaService.crearTarjeta(tarjetaDTO.getTitulo(), tipo);                            
                        
                        // Y la introducimos en su columna
                        tableroService.añadirTarjetaAListaPorNombre(tableroId, listaDTO.getNombre(), nuevaTarjetaId.getValor());
                    }
                }
            }
        }
        return tableroId;
    }
}
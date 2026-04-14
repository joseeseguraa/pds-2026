package pds.gestiontareas.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import pds.gestiontareas.application.dto.plantilla.*;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class PlantillaService {
	private final TableroService tableroService;
    private final TarjetaService tarjetaService;

    public PlantillaService(TableroService tableroService, TarjetaService tarjetaService) {
        this.tableroService = tableroService;
        this.tarjetaService = tarjetaService;
    }

    public TableroId crearTableroDesdeYaml(File archivoYaml, String emailCreador) {
        try {
            // 1. Leer el archivo YAML y convertirlo a objetos Java
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            TableroPlantillaDTO plantilla = mapper.readValue(archivoYaml, TableroPlantillaDTO.class);

            // 2. Crear el Tablero base
            TableroId tableroId = tableroService.crearTablero(plantilla.getNombre(), emailCreador);

            // 3. Crear las listas
            if (plantilla.getListas() != null) {
                for (ListaPlantillaDTO listaDTO : plantilla.getListas()) {
                    
                	tableroService.añadirListaATablero(tableroId, listaDTO.getNombre());               	
                    if (listaDTO.getLimite() != null) {
                        tableroService.actualizarReglasLista(tableroId, listaDTO.getNombre(), listaDTO.getLimite(), null);
                    }

                    // Si la plantilla define un límite, se lo aplicamos
                    if (listaDTO.getLimite() != null) {
                        tableroService.actualizarReglasLista(tableroId, listaDTO.getNombre(), listaDTO.getLimite(), null);
                    }

                    // 4. Crear las tarjetas dentro de esta lista
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
            
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar la plantilla YAML: " + e.getMessage(), e);
        }
    }
}

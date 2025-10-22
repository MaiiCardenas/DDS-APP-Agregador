package ar.edu.utn.dds.k3003.model.consensos;

import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;
import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Unificador {


    public Unificador() {
    }

    public List<Hecho> unificarHechos(String nombreColeccion, List<Fuente> fuentes) {
        List<Hecho> hechos = this.todosLosHechos(nombreColeccion, fuentes);
        Map<String, Hecho> hechosUnicos = hechos.stream()
                .collect(Collectors.toMap(
                        Hecho::getTitulo,
                        Function.identity(),
                        (existente, nuevo) -> existente));
        return new ArrayList<>(hechosUnicos.values());
    }

    public List<Hecho> todosLosHechos(String coleccion, List<Fuente> fuentes){
        List<Hecho> hechos = new ArrayList<>();
        for (Fuente fuente : fuentes) {
            if (fuente.getEndpoint() != null) {
                try {
                    List<HechoDTO> hechosDTO = fuente.obtenerHechos(coleccion);
                    hechos.addAll(
                            hechosDTO.stream()
                                    .map(dto -> {
                                        Hecho hecho = new Hecho(dto.getTitulo(), dto.getId(), dto.getNombreColeccion());
                                        hecho.setOrigen(fuente.getId());
                                        return hecho;
                                    }).toList());
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
        }
        return hechos;
    }
}

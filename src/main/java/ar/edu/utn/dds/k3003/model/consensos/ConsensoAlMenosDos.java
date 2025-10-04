package ar.edu.utn.dds.k3003.model.consensos;

import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConsensoAlMenosDos implements Consenso{

    public ConsensoAlMenosDos() {}
    @Override
    public List<Hecho> obtenerHechos(List<Fuente> fuentes, String coleccion) {
        Unificador unificador = new Unificador();
        if (fuentes.size() == 1) {
            return unificador.unificarHechos(coleccion, fuentes);
        } else {
            List<Hecho> hechos = unificador.todosLosHechos(coleccion, fuentes);
            Set<String> titulos_Repetidos = hechos.stream()
                    .collect(Collectors.groupingBy(Hecho::getTitulo,
                            Collectors.mapping(Hecho::getOrigen, Collectors.toSet())))
                    .entrySet().stream()
                    .filter(e -> e.getValue().size() >= 2)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            return hechos.stream().filter(h -> titulos_Repetidos.contains(h.getTitulo()))
                    .collect(Collectors.toMap(
                            Hecho::getTitulo, Function.identity(),
                            (h1, h2) -> h1))
                    .values().stream().collect(Collectors.toList());
        }
    }

}

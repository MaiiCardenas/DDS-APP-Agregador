package ar.edu.utn.dds.k3003.model.consensos;

import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConsensoTodos implements Consenso{

    private Unificador unificador = new Unificador();
    @Override
    public List<Hecho> obtenerHechos(List<Fuente> fuentes, String coleccion) {

        return unificador.unificarHechos(coleccion, fuentes);
    }
}

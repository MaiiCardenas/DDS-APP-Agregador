package ar.edu.utn.dds.k3003.model.consensos;

import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConsensoTodos implements Consenso{

    public ConsensoTodos() {}
    @Override
    public List<Hecho> obtenerHechos(List<Fuente> fuentes, String coleccion) {
        Unificador unificador = new Unificador();

        return unificador.unificarHechos(coleccion, fuentes);
    }
}

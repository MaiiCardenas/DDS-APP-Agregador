package ar.edu.utn.dds.k3003.model.consensos;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.ConexionHTTP;
import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConsensoEstricto implements Consenso{

    public ConsensoEstricto() {}
    private ConexionHTTP conexion = new ConexionHTTP(new RestTemplate());
    @Override
    public List<Hecho> obtenerHechos(List<Fuente> fuentes, String coleccion) {

        Unificador unificador = new Unificador();
        List<Hecho> hechos = unificador.unificarHechos(coleccion, fuentes);
        Map<Hecho, Boolean> mapHechos = conexion.consultarLote(hechos);
        mapHechos.entrySet().removeIf(entry -> entry.getValue() == true);
        return new ArrayList<>(mapHechos.keySet()).stream().toList();
    }
}
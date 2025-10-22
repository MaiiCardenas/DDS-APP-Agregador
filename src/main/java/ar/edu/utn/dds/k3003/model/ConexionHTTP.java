package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ConexionHTTP {

    private RestTemplate restTemplate;

    public ConexionHTTP() {
        this.restTemplate = new RestTemplate();
    }

    public List<HechoDTO> obtenerHechosPorColeccion(String nombreColeccion, String endpoint) {
        String url = endpoint + "/colecciones/" + nombreColeccion + "/hechos";
        ResponseEntity<HechoDTO[]> response = restTemplate.getForEntity(url, HechoDTO[].class);
        HechoDTO[] hechosArray = response.getBody();
        if (hechosArray == null) {
            return List.of();
        }
        return Arrays.asList(hechosArray);
    }
}


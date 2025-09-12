package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ConexionHTTP {

    private String endpoint;
    private RestTemplate restTemplate;

    public ConexionHTTP(String endpoint) {
        this.endpoint = endpoint;
        this.restTemplate = new RestTemplate();
    }

    public List<HechoDTO> obtenerHechosPorColeccion(String nombreColeccion) {
        String url = this.endpoint + "/coleccion/" + nombreColeccion + "/hechos";
        ResponseEntity<HechoDTO[]> response = restTemplate.getForEntity(url, HechoDTO[].class);
        HechoDTO[] hechosArray = response.getBody();
        if (hechosArray == null) {
            return List.of();
        }
        return Arrays.asList(hechosArray);
    }
}


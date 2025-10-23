package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Service
public class ConexionHTTP {
    private static final Logger logger = LoggerFactory.getLogger(ConexionHTTP.class);

    @Autowired
    private RestTemplate restTemplate;

    public ConexionHTTP() {
        //this.restTemplate = new RestTemplate();
    }

    public List<HechoDTO> obtenerHechosPorColeccion(String nombreColeccion, String endpoint) {
        String url = endpoint + "/colecciones/" + nombreColeccion + "/hechos";
        logger.info("Solicitando la coleccion {} a la API-FUENTE", nombreColeccion);
        ResponseEntity<HechoDTO[]> response = restTemplate.getForEntity(url, HechoDTO[].class);
        logger.info("Respuesta de restTemplate: {}",response.getBody());
        HechoDTO[] hechosArray = response.getBody();
        if (hechosArray == null) {
            return List.of();
        }
        return Arrays.asList(hechosArray);
    }
}


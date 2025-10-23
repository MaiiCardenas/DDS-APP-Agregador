package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Getter
@Setter
@Service
public class ConexionHTTP {
    private static final Logger logger = LoggerFactory.getLogger(ConexionHTTP.class);

    @Autowired
    private RestTemplate restTemplate;

    public ConexionHTTP() {
    }

    public List<HechoDTO> obtenerHechosPorColeccion(String nombreColeccion, String endpoint) {
        if (endpoint == null || endpoint.isEmpty() || nombreColeccion == null || nombreColeccion.isEmpty()) {
            logger.warn("Endpoint o nombre de colección inválido");
            return List.of();
        }
        String url = endpoint + "/colecciones/" + nombreColeccion + "/hechos";
        logger.info("Solicitando la colección {} a la API-FUENTE: {}", nombreColeccion, url);
        try {
            ResponseEntity<List<HechoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,  // Agrega HttpEntity si necesitas headers (ej. autenticación)
                    new ParameterizedTypeReference<List<HechoDTO>>() {}
            );
            List<HechoDTO> hechos = response.getBody();
            if (hechos == null) {
                logger.warn("Respuesta de la API es nula para: {}", url);
                return List.of();
            }
            logger.info("Obtenidos {} hechos de la colección: {}", hechos.size(), nombreColeccion);
            return hechos;
        } catch (Exception e) {
            throw new RuntimeException("Error al consumir la API" + url +"Con error:" + e.getMessage(), e);
        }
    }
}


package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType; // solo si lo usas
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ConexionHTTP {

    private static final Logger logger = LoggerFactory.getLogger(ConexionHTTP.class);
    private final RestTemplate restTemplate;

    public ConexionHTTP(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<HechoDTO> obtenerHechosPorColeccion(String nombreColeccion, String endpoint) {
        logger.info("Iniciando obtenerHechosPorColeccion: nombreColeccion={}, endpoint={}", nombreColeccion, endpoint);

        if (endpoint == null || endpoint.isBlank() || nombreColeccion == null || nombreColeccion.isBlank()) {
            logger.warn("Endpoint o nombre de colección inválido");
            return List.of();
        }

        // Construcción segura de URL (evita dobles slashes y aplica encoding del path)
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        String url = UriComponentsBuilder.fromHttpUrl(base)
                .path("/colecciones/{nombre}/hechos")
                .buildAndExpand(nombreColeccion)
                .toUriString();

        logger.info("Solicitando la colección {} a la API-FUENTE: {}", nombreColeccion, url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<HechoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<HechoDTO>>() {}
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.warn("HTTP {} al llamar {}", response.getStatusCode(), url);
                return List.of();
            }

            List<HechoDTO> hechos = response.getBody();
            if (hechos == null) {
                logger.warn("Respuesta de la API es nula para: {}", url);
                return List.of();
            }

            logger.info("Obtenidos {} hechos de la colección: {}", hechos.size(), nombreColeccion);
            return hechos;

        } catch (HttpStatusCodeException e) {
            // Muestra status y cuerpo de error para depurar
            logger.error("HTTP {} al consumir {}. Body: {}", e.getStatusCode(), url, e.getResponseBodyAsString(), e);
            return List.of();
        } catch (RestClientException e) {
            logger.error("Error RestTemplate al consumir {}: {}", url, e.getMessage(), e);
            return List.of();
        } catch (Exception e) {
            logger.error("Error inesperado en obtenerHechosPorColeccion: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
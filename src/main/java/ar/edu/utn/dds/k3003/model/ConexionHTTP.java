package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        String url = UriComponentsBuilder.fromHttpUrl(base)
                .path("/colecciones/{nombre}/hechos")
                .buildAndExpand(nombreColeccion)
                .toUriString();

        logger.info("Solicitando la colección {} a la API-FUENTE: {}", nombreColeccion, url);

        HttpHeaders headers = new HttpHeaders(); // el interceptor añade User-Agent y Accept
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        final int maxAttempts = 3;
        final long baseDelayMs = 1000; // 1s

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                ResponseEntity<List<HechoDTO>> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity,
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
                if (e.getStatusCode().value() == 429 && attempt < maxAttempts) {
                    long waitMs = resolveWaitMsFromRetryAfter(e.getResponseHeaders(), baseDelayMs, attempt);
                    logger.warn("HTTP 429 Too Many Requests (intento {}/{}). Reintentando en {} ms. Body: {}",
                            attempt, maxAttempts, waitMs, e.getResponseBodyAsString());
                    sleepSilently(waitMs);
                    continue; // reintenta
                }
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

        logger.error("Agotados {} intentos por 429 al consumir {}", maxAttempts, url);
        return List.of();
    }

    private static long resolveWaitMsFromRetryAfter(HttpHeaders headers, long baseDelayMs, int attempt) {
        if (headers != null) {
            String ra = headers.getFirst("Retry-After");
            if (ra != null) {
                try {
                    long seconds = Long.parseLong(ra.trim());
                    return seconds * 1000;
                } catch (NumberFormatException ignore) {
                    // Si viene como fecha HTTP (RFC 1123), podrías parsearla aquí.
                }
            }
        }
        long backoff = (long) (baseDelayMs * Math.pow(2, attempt - 1)); // 1s, 2s, 4s...
        long jitter = ThreadLocalRandom.current().nextLong(100, 300);
        return backoff + jitter;
    }

    private static void sleepSilently(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}

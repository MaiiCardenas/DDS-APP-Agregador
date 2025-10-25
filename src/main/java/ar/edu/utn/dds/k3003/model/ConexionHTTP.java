package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;
import ar.edu.utn.dds.k3003.model.DTO.MiniHechoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ConexionHTTP {

    private static final Logger logger = LoggerFactory.getLogger(ConexionHTTP.class);
    private final RestTemplate restTemplate;

    public ConexionHTTP(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean agregarHechoAFuente(String endpoint, MiniHechoDTO miniHecho) {
        String url = endpoint.concat("/hecho");
        logger.info("Iniciando agregarHechoAFuente: url={}, miniHecho={}", url, miniHecho);
        if (url == null || url.isBlank() || miniHecho == null) {
            logger.warn("URL o MiniHecho inválido");
            return false;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniHechoDTO> entity = new HttpEntity<>(miniHecho, headers);
        final int maxAttempts = 3;
        final long baseDelayMs = 1000;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                ResponseEntity<Void> response = restTemplate.exchange(
                        url, HttpMethod.POST, entity, Void.class
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    logger.info("Hecho agregado exitosamente a la fuente: {}", url);
                    return true;
                } else {
                    logger.warn("HTTP {} al agregar hecho a {}", response.getStatusCode(), url);
                    return false;
                }
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode().value() == 429 && attempt < maxAttempts) {
                    long waitMs = resolveWaitMsFromRetryAfter(e.getResponseHeaders(), baseDelayMs, attempt);
                    logger.warn("HTTP 429 (intento {}/{}). Reintentando en {} ms. Body: {}",
                            attempt, maxAttempts, waitMs, e.getResponseBodyAsString());
                    sleepSilently(waitMs);
                    continue;
                }
                logger.error("HTTP {} al agregar hecho a {}. Body: {}", e.getStatusCode(), url, e.getResponseBodyAsString(), e);
                return false;
            } catch (RestClientException e) {
                logger.error("Error RestTemplate al agregar a {}: {}", url, e.getMessage(), e);
                return false;
            } catch (Exception e) {
                logger.error("Error inesperado en agregarHechoAFuente: {}", e.getMessage(), e);
                return false;
            }
        }
        logger.error("Agotados {} intentos por 429 al agregar hecho a {}", maxAttempts, url);
        return false;
    }


        public Map<Hecho, Boolean> consultarLote(List<Hecho> listaHechos) {
        logger.info("Iniciando consultarLote con {} hechos", listaHechos != null ? listaHechos.size() : 0);
        if (listaHechos == null || listaHechos.isEmpty()) {
            logger.warn("Lista de hechos vacía o nula");
            return Map.of();
        }
        Map<Hecho, Boolean> resultados = new HashMap<>();
        final long delayMs = 1000;  // Delay de 1s entre consultas; ajusta según API (ej. 2000 para más cautela)
        for (Hecho hecho : listaHechos) {
            try {
                boolean tiene = this.tieneSolicitud(hecho);  // Llama al método existente
                resultados.put(hecho, tiene);
                logger.debug("Procesado hecho {}: tieneSolicitud={}", hecho.getId(), tiene);
                // Delay para evitar 429, excepto en la última iteración
                if (!hecho.equals(listaHechos.get(listaHechos.size() - 1))) {
                    sleepSilently(delayMs);
                }
            } catch (Exception e) {
                logger.error("Error al procesar hecho {} en lote: {}", hecho.getId(), e.getMessage(), e);
                resultados.put(hecho, false);  // Asume false en error, o maneja como prefieras
            }
        }
        logger.info("Completado consultarLote: procesados {} hechos", resultados.size());
        return resultados;
    }

    private boolean tieneSolicitud(Hecho hecho) {
        logger.info("Iniciando tieneSolicitud: hechoId={}", hecho != null ? hecho.getId() : "null");

        if (hecho == null || hecho.getId() == null) {
            logger.warn("Hecho inválido o nulo");
            return false;
        }

        String url = "https://dds-app-solicitud.onrender.com/api/solicitudes?hecho=" + hecho.getId();
        logger.info("Consultando solicitudes para hecho {} en: {}", hecho.getId(), url);

        HttpHeaders headers = new HttpHeaders(); // El interceptor añade User-Agent y Accept si es necesario
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        final int maxAttempts = 3;
        final long baseDelayMs = 1000; // 1s

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                ResponseEntity<SolicitudDTO[]> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, SolicitudDTO[].class
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                    logger.warn("HTTP {} al consultar solicitudes para hecho {}", response.getStatusCode(), hecho.getId());
                    return false; // O podrías reintentar en otros códigos, pero aquí asumimos no
                }

                SolicitudDTO[] solicitudes = response.getBody();
                boolean tiene = (solicitudes != null && solicitudes.length > 0);
                logger.info("Hecho {} tiene solicitudes: {}", hecho.getId(), tiene);
                return tiene;

            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode().value() == 429 && attempt < maxAttempts) {
                    long waitMs = resolveWaitMsFromRetryAfter(e.getResponseHeaders(), baseDelayMs, attempt);
                    logger.warn("HTTP 429 (intento {}/{}). Reintentando en {} ms para hecho {}. Body: {}",
                            attempt, maxAttempts, waitMs, hecho.getId(), e.getResponseBodyAsString());
                    sleepSilently(waitMs);
                    continue;
                }
                logger.error("HTTP {} al consultar solicitudes para hecho {}. Body: {}",
                        e.getStatusCode(), hecho.getId(), e.getResponseBodyAsString(), e);
                return false;
            } catch (RestClientException e) {
                logger.error("Error RestTemplate al consultar {}: {}", url, e.getMessage(), e);
                return false;
            } catch (Exception e) {
                logger.error("Error inesperado en tieneSolicitud: {}", e.getMessage(), e);
                return false;
            }
        }

        logger.error("Agotados {} intentos por 429 al consultar solicitudes para hecho {}", maxAttempts, hecho.getId());
        return false;
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

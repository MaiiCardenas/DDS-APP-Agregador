package ar.edu.utn.dds.k3003;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Timeouts en HttpClient 5.x
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))      // timeout al conectar
                .setResponseTimeout(Timeout.ofSeconds(10))    // timeout esperando la respuesta
                .build();

        // Cliente HTTP con la config de timeouts
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();  // NO usar setConnectionManagerTimeout en 5.x

        // Fábrica que usa el cliente 5.x
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(factory);
        return rt;
    }
}
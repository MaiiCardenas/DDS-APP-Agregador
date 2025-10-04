package ar.edu.utn.dds.k3003.model.consensos;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsensoEstricto implements Consenso{
    private Unificador unificador = new Unificador();
    @Override
    public List<Hecho> obtenerHechos(List<Fuente> fuentes, String coleccion) {
        List<Hecho> hechos = unificador.unificarHechos(coleccion, fuentes);
        List<Hecho> hechosBuenos = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        for(Hecho hecho : hechos){
            String request = "https://dds-app-solicitud.onrender.com/api/solicitudes?hecho="+ hecho.getId();
            ResponseEntity<SolicitudDTO[]> response = restTemplate.getForEntity(request, SolicitudDTO[].class );
            SolicitudDTO[] solicitudes = response.getBody();
            if(solicitudes == null){
                hechosBuenos.add(hecho);
            }
        }
        return hechosBuenos;
    }
}
package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaAgregador;
import ar.edu.utn.dds.k3003.facades.dtos.ConsensosEnum;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consenso")
public class ConsensoController {

    private final Fachada fachadaAgregador;

    public ConsensoController(Fachada fachadaAgregador) {
        this.fachadaAgregador = fachadaAgregador;
    }
    @PatchMapping
    public ResponseEntity<Void> configurarConsenso(@RequestBody Map<String, String> body) {
        String tipoStr = body.get("tipo");
        String coleccion = body.get("coleccion");
        if (tipoStr == null || coleccion == null) {
            return ResponseEntity.badRequest().build();
        }
        ConsensosEnum consenso;
        try {
            consenso = ConsensosEnum.valueOf(tipoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        fachadaAgregador.setConsensoStrategy(consenso, coleccion);
        return ResponseEntity.noContent().build();
    }


}
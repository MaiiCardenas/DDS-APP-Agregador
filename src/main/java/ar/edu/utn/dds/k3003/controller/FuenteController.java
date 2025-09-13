package ar.edu.utn.dds.k3003.controller;

import java.util.List;

import ar.edu.utn.dds.k3003.app.Fachada;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.edu.utn.dds.k3003.facades.FachadaAgregador;
import ar.edu.utn.dds.k3003.facades.dtos.FuenteDTO;

@RestController
@RequestMapping("/fuentes")
public class FuenteController {

    private final Fachada fachadaAgregador;

    public FuenteController(Fachada fachadaAgregador) {
        this.fachadaAgregador = fachadaAgregador;
    }

    @GetMapping
    public ResponseEntity<List<FuenteDTO>> fuentes() {
        return ResponseEntity.ok(fachadaAgregador.fuentes());
    }

    @PostMapping
    public ResponseEntity<FuenteDTO> agregarFuente(@RequestBody FuenteDTO fuenteDTO) {
        return ResponseEntity.ok(fachadaAgregador.agregar(fuenteDTO));
    }
    @DeleteMapping
    public ResponseEntity<Void> borrarTodasLasFuentes() {
        fachadaAgregador.borrarTodasLasFuentes();
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

}

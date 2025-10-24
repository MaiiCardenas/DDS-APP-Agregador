package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaAgregador;
import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;

import io.javalin.http.OkResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coleccion")
public class ColeccionController {

    private final Fachada fachadaAgregador;

    public ColeccionController(Fachada fachadaAgregador) {
        this.fachadaAgregador = fachadaAgregador;
    }

    @GetMapping("/{nombre}/hechos")
    public ResponseEntity<List<HechoDTO>> listarHechosPorColeccion(@PathVariable String nombre) {
        return new ResponseEntity<List<HechoDTO>>(fachadaAgregador.hechos(nombre), HttpStatus.OK);
    }
    @PostMapping("/{nombre}")
    public ResponseEntity<String> postColeccion(@PathVariable String nombre){
        return new ResponseEntity<String>(fachadaAgregador.nuevaColeccion(nombre),HttpStatus.OK);
    }

}
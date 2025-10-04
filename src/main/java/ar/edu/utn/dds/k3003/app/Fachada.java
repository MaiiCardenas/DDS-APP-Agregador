package ar.edu.utn.dds.k3003.app;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import ar.edu.utn.dds.k3003.model.Coleccion;
import ar.edu.utn.dds.k3003.model.consensos.ConsensoAlMenosDos;
import ar.edu.utn.dds.k3003.model.consensos.ConsensoEstricto;
import ar.edu.utn.dds.k3003.model.consensos.ConsensoTodos;
import ar.edu.utn.dds.k3003.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.facades.dtos.FuenteDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.model.consensos.ConsensoEnum;


@Service
public class Fachada{


  private final FuenteRepository fuenteRepository;

  private final ColeccionRepository coleccionRepository;

  @Autowired
  public Fachada(JpaFuenteRepository fuenteRepo, JpaColeccionRepository coleccionRepo) {
    this.fuenteRepository = fuenteRepo;
    this.coleccionRepository = coleccionRepo;
  }


  public FuenteDTO agregar(FuenteDTO fuenteDto) {
    String id = UUID.randomUUID().toString();
    Fuente fuente = new Fuente(id, fuenteDto.nombre(), fuenteDto.endpoint());
    fuenteRepository.save(fuente);
    return convertirAFuenteDTO(fuente);
  }

  public List<FuenteDTO> fuentes() {
    return fuenteRepository.findAll().stream().map(this::convertirAFuenteDTO).collect(Collectors.toList());
  }


  public FuenteDTO buscarFuenteXId(String fuenteId) throws NoSuchElementException {
    return fuenteRepository.findById(fuenteId)
        .map(this::convertirAFuenteDTO)
        .orElseThrow(() -> new NoSuchElementException("Fuente no encontrada: " + fuenteId));
  }


  public List<HechoDTO> hechos(String nombreColeccion) throws NoSuchElementException {
    List<Fuente> listaFuentes = fuenteRepository.findAll();
    List<Hecho> hechosModelo = coleccionRepository.findById(nombreColeccion).get()
            .obtenerHechos(listaFuentes);

    if (hechosModelo == null || hechosModelo.isEmpty()) {
      throw new NoSuchElementException("Busqueda no encontrada de: " + nombreColeccion);
    }
    return hechosModelo.stream()
        .map(this::convertirADTO)
        .collect(Collectors.toList());
  }

  public void setConsensoStrategy(ConsensoEnum tipoConsenso, String nombreColeccion){
    Optional<Coleccion> coleccion = coleccionRepository.findById(nombreColeccion);
    Coleccion laColeccion = coleccion.get();
    switch (tipoConsenso){
      case AL_MENOS_2 -> laColeccion.setConsenso(new ConsensoAlMenosDos());
      case TODOS -> laColeccion.setConsenso(new ConsensoTodos());
      case ESTRICTO -> laColeccion.setConsenso(new ConsensoEstricto());
    }
  }

  private HechoDTO convertirADTO(Hecho hecho) {
    return new HechoDTO(hecho.getId(), hecho.getColeccionNombre(), hecho.getTitulo());
  }

  private FuenteDTO convertirAFuenteDTO(Fuente fuente) {
    return new FuenteDTO(fuente.getId(), fuente.getNombre(), fuente.getEndpoint());
  }
  public void borrarTodasLasFuentes() {
    fuenteRepository.deleteAll();
  }


}
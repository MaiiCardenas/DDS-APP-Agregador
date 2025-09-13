package ar.edu.utn.dds.k3003.app;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import ar.edu.utn.dds.k3003.model.Coleccion;
import ar.edu.utn.dds.k3003.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.facades.FachadaAgregador;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ConsensosEnum;
import ar.edu.utn.dds.k3003.facades.dtos.FuenteDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.model.Agregador;
import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;

@Service
public class Fachada{

  private Agregador agregador = new Agregador();

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
    ConsensosEnum consenso = coleccionRepository.findById(nombreColeccion).get().getConsenso();
    List<Hecho> hechosModelo = agregador.obtenerHechosPorColeccion(nombreColeccion, consenso, listaFuentes);

    if (hechosModelo == null || hechosModelo.isEmpty()) {
      throw new NoSuchElementException("Busqueda no encontrada de: " + nombreColeccion);
    }
    return hechosModelo.stream()
        .map(this::convertirADTO)
        .collect(Collectors.toList());
  }


  /*public void addFachadaFuentes(String fuenteId, FachadaFuente fuente) {
    agregador.agregarFachadaAFuente(fuenteId, fuente);
  }*/

  public void setConsensoStrategy(ConsensosEnum tipoConsenso, String nombreColeccion){
    Optional<Coleccion> coleccion = coleccionRepository.findById(nombreColeccion);
    if(coleccion.isEmpty()){
      Coleccion nuevaColeccion = new Coleccion(nombreColeccion);
      nuevaColeccion.setConsenso(tipoConsenso);
      coleccionRepository.save(nuevaColeccion);
    }else{
      Coleccion laColeccion = coleccion.get();
      laColeccion.setConsenso(tipoConsenso);
      coleccionRepository.save(laColeccion);
    }}


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
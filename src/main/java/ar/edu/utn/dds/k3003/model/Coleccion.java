package ar.edu.utn.dds.k3003.model;

import java.util.List;

import ar.edu.utn.dds.k3003.model.consensos.Consenso;
import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Data
@Entity
public class Coleccion {

  public Coleccion(String nombre) {
    this.nombre = nombre;
  }

  @Id
  private String nombre;
  private Consenso consenso;

  public List<Hecho> obtenerHechos(List<Fuente> fuentes){
    return this.consenso.obtenerHechos(fuentes, this.nombre);
  }
}
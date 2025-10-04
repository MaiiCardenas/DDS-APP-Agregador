package ar.edu.utn.dds.k3003.model;

import java.util.List;

import ar.edu.utn.dds.k3003.model.consensos.Consenso;
import ar.edu.utn.dds.k3003.model.consensos.ConsensoEnum;
import ar.edu.utn.dds.k3003.model.consensos.Unificador;
import jakarta.persistence.Transient;
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
  private ConsensoEnum enumConsenso;
  @Transient
  private Consenso consenso;

  public Coleccion() {
  }

  public List<Hecho> obtenerHechos(List<Fuente> fuentes){
    Unificador unificador = new Unificador();
    this.consenso = unificador.miConsenso(enumConsenso);
    return this.consenso.obtenerHechos(fuentes, this.nombre);
  }
}
package ar.edu.utn.dds.k3003.model.consensos;

import ar.edu.utn.dds.k3003.model.Fuente;
import ar.edu.utn.dds.k3003.model.Hecho;

import java.util.List;

/**
 * Interfaz Consenso: Define el contrato para obtener hechos con un consenso determinado.
 */
public interface Consenso {
    public List<Hecho> obtenerHechos(List<Fuente> fuentes, String coleccion);
}

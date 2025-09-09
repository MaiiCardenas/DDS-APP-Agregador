package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FachadaFT {
    private String id;
    private Fuente fuente;
    private FachadaFuente fachada;
    public FachadaFT(String fuenteID, FachadaFuente fachada1) {
        this.id = fuenteID;
        this.fachada = fachada1;
    }
}

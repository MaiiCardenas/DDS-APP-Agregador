package ar.edu.utn.dds.k3003.model.DTO;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;
@Data
public class MiniHechoDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("nombre_coleccion")
    private String nombreColeccion;
    @JsonProperty("titulo")
    private String titulo;

    public MiniHechoDTO() {
    }
    public MiniHechoDTO(String id, String nombreColeccion, String titulo) {
        this.id = id;
        this.nombreColeccion = nombreColeccion;
        this.titulo = titulo;
    }
}

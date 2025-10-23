package ar.edu.utn.dds.k3003.model.DTO;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.List;

//@JsonPropertyOrder({ "id", "nombre_coleccion", "titulo", "etiquetas", "categoria","ubicacion","fecha","origen" })
public class HechoDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("nombre_coleccion")
    private String nombreColeccion;
    @JsonProperty("titulo")
    private String titulo;
    @JsonProperty("etiquetas")
    private List<String> etiquetas;
    @JsonProperty("categoria")
    private CategoriaHechoEnum categoria;
    @JsonProperty("ubicacion")
    private String ubicacion;
    @JsonProperty("fecha")
    private LocalDateTime fecha;
    @JsonProperty("origen")
    private String origen;

    public HechoDTO() {
    }

    public HechoDTO(String id, String nombreColeccion, String titulo) {
        this.id = id;
        this.nombreColeccion = nombreColeccion;
        this.titulo = titulo;
        this.categoria = null;
        this.fecha = null;
        this.origen = null;
        this.ubicacion = null;
        this.etiquetas = null;
    }

    public HechoDTO(String id, String nombreColeccion, String titulo, List<String> etiquetas, CategoriaHechoEnum categoria, String ubicacion, LocalDateTime fecha, String origen) {
        this.id = id;
        this.nombreColeccion = nombreColeccion;
        this.titulo = titulo;
        this.etiquetas = etiquetas;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.origen = origen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreColeccion() {
        return nombreColeccion;
    }

    public void setNombreColeccion(String nombreColeccion) {
        this.nombreColeccion = nombreColeccion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public CategoriaHechoEnum getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaHechoEnum categoria) {
        this.categoria = categoria;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}
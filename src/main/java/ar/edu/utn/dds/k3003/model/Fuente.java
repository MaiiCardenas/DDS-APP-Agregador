package ar.edu.utn.dds.k3003.model;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.model.DTO.HechoDTO;
import ar.edu.utn.dds.k3003.model.DTO.MiniHechoDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;

@Data
@Entity
@Table(name = "Fuente")
public class Fuente {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Transient
    private FachadaFuente fachadaFuente;

    @OneToMany(mappedBy = "fuente", cascade = CascadeType.ALL)
    private List<Hecho> lista_hechos = new ArrayList<>();

    @Transient
    private ConexionHTTP conexionHTTP;

    public List<HechoDTO> obtenerHechos(String coleccionId) {
        return conexionHTTP.obtenerHechosPorColeccion(coleccionId, endpoint);
    }

    public boolean agregarHecho(MiniHechoDTO miniHecho){
        return conexionHTTP.agregarHechoAFuente(this.endpoint, miniHecho);
    }

    public Fuente(String id, String nombre, String endpoint) {
        this.id = id;
        this.nombre = nombre;
        this.endpoint = endpoint;
    }

    public Fuente() {
        this.conexionHTTP = new ConexionHTTP(new RestTemplate());
    }
}

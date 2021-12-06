package pfc.consignacionhacienda.model;

import javax.persistence.*;
import java.util.List;
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    private String cuit;
    private Boolean deleted;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Provenance> provenances;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public List<Provenance> getProvenances() {
        return provenances;
    }

    public void setProvenances(List<Provenance> provenances) {
        this.provenances = provenances;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cuit='" + cuit + '\'' +
                ", provenances=" + provenances +
                '}';
    }
}

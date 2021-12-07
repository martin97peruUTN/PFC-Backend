package pfc.consignacionhacienda.dto;

import pfc.consignacionhacienda.model.Provenance;
import java.util.List;

public class ClientDTO {
    private Integer id;
    private String name;
    private String cuit;
    private Boolean deleted;
    private List<Provenance> provenances;
    private List<Provenance> deletedProvenances;

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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<Provenance> getProvenances() {
        return provenances;
    }

    public void setProvenances(List<Provenance> provenances) {
        this.provenances = provenances;
    }

    public List<Provenance> getDeletedProvenances() {
        return deletedProvenances;
    }

    public void setDeletedProvenances(List<Provenance> deletedProvenances) {
        this.deletedProvenances = deletedProvenances;
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cuit='" + cuit + '\'' +
                ", deleted=" + deleted +
                ", provenances=" + provenances +
                ", provenancesDeleted=" + deletedProvenances +
                '}';
    }
}

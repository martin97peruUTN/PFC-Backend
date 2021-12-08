package pfc.consignacionhacienda.dto;

import pfc.consignacionhacienda.model.Locality;

public class ProvenanceDTO {
    private Integer id;
    private String reference;
    private String renspaNumber;
    private Boolean deleted;
    private Locality locality;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRenspaNumber() {
        return renspaNumber;
    }

    public void setRenspaNumber(String renspaNumber) {
        this.renspaNumber = renspaNumber;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    @Override
    public String toString() {
        return "ProvenanceDTO{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", renspaNumber='" + renspaNumber + '\'' +
                ", deleted=" + deleted +
                ", locality=" + locality +
                '}';
    }
}

package pfc.consignacionhacienda.dto;


import pfc.consignacionhacienda.model.Provenance;

public class BatchDTO {
    private Integer id;
    private Provenance provenance;
    private Integer corralNumber;
    private String dteNumber;
    private Boolean deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Provenance getProvenance() {
        return provenance;
    }

    public void setProvenance(Provenance provenance) {
        this.provenance = provenance;
    }

    public Integer getCorralNumber() {
        return corralNumber;
    }

    public void setCorralNumber(Integer corralNumber) {
        this.corralNumber = corralNumber;
    }

    public String getDteNumber() {
        return dteNumber;
    }

    public void setDteNumber(String dteNumber) {
        this.dteNumber = dteNumber;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "BatchDTO{" +
                "id=" + id +
                ", provenance=" + provenance +
                ", corralNumber=" + corralNumber +
                ", dteNumber=" + dteNumber +
                ", deleted=" + deleted +
                '}';
    }
}

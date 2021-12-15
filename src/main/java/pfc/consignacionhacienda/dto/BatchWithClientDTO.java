package pfc.consignacionhacienda.dto;

import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Provenance;

import java.util.List;

public class BatchWithClientDTO {
    private Integer id;
    private Integer corralNumber;
    private String dteNumber;
    private Provenance provenance;
    private ClientForBatchDTO client;
    List<AnimalsOnGround> animalsOnGround;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Provenance getProvenance() {
        return provenance;
    }

    public void setProvenance(Provenance provenance) {
        this.provenance = provenance;
    }

    public ClientForBatchDTO getClient() {
        return client;
    }

    public void setClient(ClientForBatchDTO client) {
        this.client = client;
    }

    public List<AnimalsOnGround> getAnimalsOnGround() {
        return animalsOnGround;
    }

    public void setAnimalsOnGround(List<AnimalsOnGround> animalsOnGround) {
        this.animalsOnGround = animalsOnGround;
    }

    @Override
    public String toString() {
        return "BatchWithClientDTO{" +
                "id=" + id +
                ", corralNumber=" + corralNumber +
                ", dteNumber='" + dteNumber + '\'' +
                ", provenance=" + provenance +
                ", client=" + client +
                ", animalsOnGround=" + animalsOnGround +
                '}';
    }
}

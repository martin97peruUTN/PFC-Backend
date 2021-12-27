package pfc.consignacionhacienda.dto;

import pfc.consignacionhacienda.model.Client;

public class SoldBatchDTO {
    private Integer id;

    private String dteNumber;

    private Integer amount;

    private Double price;

    private Boolean mustWeigh;

    private Double weight;

    private Boolean deleted;

    private Client client;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDteNumber() {
        return dteNumber;
    }

    public void setDteNumber(String dteNumber) {
        this.dteNumber = dteNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getMustWeigh() {
        return mustWeigh;
    }

    public void setMustWeigh(Boolean mustWeigh) {
        this.mustWeigh = mustWeigh;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "SoldBatchDTO{" +
                "id=" + id +
                ", dteNumber='" + dteNumber + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", mustWeigh=" + mustWeigh +
                ", weight=" + weight +
                ", deleted=" + deleted +
                ", client=" + client +
                '}';
    }
}

package pfc.consignacionhacienda.dto;

import pfc.consignacionhacienda.model.Category;
import pfc.consignacionhacienda.model.Client;

public class SoldBatchResponseDTO {
    private Client buyer;
    private Client seller;
    private Integer amount;
    private Category category;
    private Boolean mustWeigh;
    private Double weight;
    private Double price;
    private String dteNumber;

    public Client getBuyer() {
        return buyer;
    }

    public void setBuyer(Client buyer) {
        this.buyer = buyer;
    }

    public Client getSeller() {
        return seller;
    }

    public void setSeller(Client seller) {
        this.seller = seller;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDteNumber() {
        return dteNumber;
    }

    public void setDteNumber(String dteNumber) {
        this.dteNumber = dteNumber;
    }

    @Override
    public String toString() {
        return "SoldBatchResponseDTO{" +
                "buyer=" + buyer +
                ", seller=" + seller +
                ", amount=" + amount +
                ", category=" + category +
                ", mustWeigh=" + mustWeigh +
                ", weight=" + weight +
                ", price=" + price +
                ", dteNumber='" + dteNumber + '\'' +
                '}';
    }
}

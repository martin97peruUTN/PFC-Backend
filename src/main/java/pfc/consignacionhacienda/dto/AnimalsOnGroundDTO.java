package pfc.consignacionhacienda.dto;

import pfc.consignacionhacienda.model.Category;
import pfc.consignacionhacienda.model.Client;

public class AnimalsOnGroundDTO {
    private Integer id;
    private Integer corralNumber;
    private Category category;
    private Client seller;
    private Integer amount;
    private Integer soldAmount;
    private Boolean sold;
    private Boolean notSold;

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Integer getSoldAmount() {
        return soldAmount;
    }

    public void setSoldAmount(Integer soldAmount) {
        this.soldAmount = soldAmount;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    public Boolean getNotSold() {
        return notSold;
    }

    public void setNotSold(Boolean notSold) {
        this.notSold = notSold;
    }

    @Override
    public String toString() {
        return "AnimalsOnGroundDTO{" +
                "id=" + id +
                ", corralNumber=" + corralNumber +
                ", category=" + category +
                ", seller=" + seller +
                ", amount=" + amount +
                ", soldAmount=" + soldAmount +
                ", sold=" + sold +
                ", notSold=" + notSold +
                '}';
    }
}

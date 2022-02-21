package pfc.consignacionhacienda.reports.dto;

import java.util.List;

public class CommonInfo {

    private String name;
    private Integer totalAnimalsSold;
    private Integer totalAnimalsNotSold;
    private Double totalMoneyIncome;
    private List<Buyer> buyers;
    private List<Seller> sellers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalAnimalsSold() {
        return totalAnimalsSold;
    }

    public void setTotalAnimalsSold(Integer totalAnimalsSold) {
        this.totalAnimalsSold = totalAnimalsSold;
    }

    public Integer getTotalAnimalsNotSold() {
        return totalAnimalsNotSold;
    }

    public void setTotalAnimalsNotSold(Integer totalAnimalsNotSold) {
        this.totalAnimalsNotSold = totalAnimalsNotSold;
    }

    public Double getTotalMoneyIncome() {
        return totalMoneyIncome;
    }

    public void setTotalMoneyIncome(Double totalMoneyIncome) {
        this.totalMoneyIncome = totalMoneyIncome;
    }

    public List<Buyer> getBuyers() {
        return buyers;
    }

    public void setBuyers(List<Buyer> buyers) {
        this.buyers = buyers;
    }

    public List<Seller> getSellers() {
        return sellers;
    }

    public void setSellers(List<Seller> sellers) {
        this.sellers = sellers;
    }


    @Override
    public String toString() {
        return "CommonInfo{" +
                "name='" + name + '\'' +
                ", totalAnimalsSold=" + totalAnimalsSold +
                ", totalAnimalsNotSold=" + totalAnimalsNotSold +
                ", totalMoneyIncome=" + totalMoneyIncome +
                ", buyers=" + buyers +
                ", sellers=" + sellers +
                '}';
    }
}

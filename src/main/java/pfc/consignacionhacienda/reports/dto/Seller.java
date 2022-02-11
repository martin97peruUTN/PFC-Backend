package pfc.consignacionhacienda.reports.dto;

public class Seller {

    private Integer id;
    private String name;
    private Integer totalAnimalsSold;
    private Integer totalAnimalsNotSold;
    private Double totalMoneyIncome;

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

    @Override
    public String toString() {
        return "Seller{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalAnimalsSold=" + totalAnimalsSold +
                ", totalAnimalsNotSold=" + totalAnimalsNotSold +
                ", totalMoneyIncome=" + totalMoneyIncome +
                '}';
    }
}

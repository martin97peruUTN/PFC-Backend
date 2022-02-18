package pfc.consignacionhacienda.reports.dto;

public class Buyer {

    private Integer id;
    private String name;
    private Integer totalBought;
    private Double totalMoneyInvested;

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

    public Integer getTotalBought() {
        return totalBought;
    }

    public void setTotalBought(Integer totalBought) {
        this.totalBought = totalBought;
    }

    public Double getTotalMoneyInvested() {
        return totalMoneyInvested;
    }

    public void setTotalMoneyInvested(Double totalMoneyInvested) {
        this.totalMoneyInvested = totalMoneyInvested;
    }

    @Override
    public String toString() {
        return "Buyer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalBought=" + totalBought +
                ", totalMoneyInvested=" + totalMoneyInvested +
                '}';
    }
}

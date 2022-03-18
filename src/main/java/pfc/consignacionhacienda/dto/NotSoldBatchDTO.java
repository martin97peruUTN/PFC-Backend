package pfc.consignacionhacienda.dto;

public class NotSoldBatchDTO {
    private Integer id;

    private String dteNumber;

    private Integer amount;

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

    @Override
    public String toString() {
        return "NotSoldBatchDTO{" +
                "id=" + id +
                ", dteNumber='" + dteNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}

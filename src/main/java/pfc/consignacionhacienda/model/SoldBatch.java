package pfc.consignacionhacienda.model;

import pfc.consignacionhacienda.services.websockets.DbListener;

import javax.persistence.*;

@Entity
@EntityListeners(DbListener.class)
public class SoldBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String dteNumber;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Double price;

    private Integer paymentTerm;

    @Column(nullable = false)
    private Boolean mustWeigh;

    private Double weight;

    private Boolean deleted;

    @ManyToOne(optional = false)
    @JoinColumn(name = "animals_id")
    private AnimalsOnGround animalsOnGround;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
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

    public AnimalsOnGround getAnimalsOnGround() {
        return animalsOnGround;
    }

    public void setAnimalsOnGround(AnimalsOnGround animalsOnGround) {
        this.animalsOnGround = animalsOnGround;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(Integer paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    @Override
    public String toString() {
        return "SoldBatch{" +
                "id=" + id +
                ", dteNumber='" + dteNumber + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", paymentTerm=" + paymentTerm +
                ", mustWeigh=" + mustWeigh +
                ", weight=" + weight +
                ", deleted=" + deleted +
                ", animalsOnGround=" + animalsOnGround +
                ", client=" + client +
                '}';
    }
}

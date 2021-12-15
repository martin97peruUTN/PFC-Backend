package pfc.consignacionhacienda.model;

import javax.persistence.*;

@Entity
public class SoldBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer dteNumber;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Boolean mustWeigh;

    private Double weight;

    @ManyToOne
    @JoinColumn(name = "animals_id")
    private AnimalsOnGround animalsOnGround;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDteNumber() {
        return dteNumber;
    }

    public void setDteNumber(Integer dteNumber) {
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

    @Override
    public String toString() {
        return "SoldBatch{" +
                "id=" + id +
                ", dteNumber=" + dteNumber +
                ", amount=" + amount +
                ", price=" + price +
                ", mustWeigh=" + mustWeigh +
                ", weight=" + weight +
                ", animalsOnGrountd=" + animalsOnGround +
                ", client=" + client +
                '}';
    }
}

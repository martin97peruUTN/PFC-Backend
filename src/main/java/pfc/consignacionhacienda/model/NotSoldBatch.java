package pfc.consignacionhacienda.model;

import javax.persistence.*;

public class NotSoldBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String dteNumber;

    @Column(nullable = false)
    private Integer amount;

//    private Boolean deleted;

    @OneToOne(optional = false)
    @JoinColumn(name = "animals_id")
    private AnimalsOnGround animalsOnGround;

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

//    public Boolean getDeleted() {
//        return deleted;
//    }
//
//    public void setDeleted(Boolean deleted) {
//        this.deleted = deleted;
//    }

    public AnimalsOnGround getAnimalsOnGround() {
        return animalsOnGround;
    }

    public void setAnimalsOnGround(AnimalsOnGround animalsOnGround) {
        this.animalsOnGround = animalsOnGround;
    }

    @Override
    public String toString() {
        return "NotSoldBatch{" +
                "id=" + id +
                ", dteNumber=" + dteNumber +
                ", amount=" + amount +
//                ", deleted=" + deleted +
                ", animalsOnGround=" + animalsOnGround +
                '}';
    }
}

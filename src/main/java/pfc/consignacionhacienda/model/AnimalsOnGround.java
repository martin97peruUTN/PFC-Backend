package pfc.consignacionhacienda.model;

import javax.persistence.*;

@Entity
public class AnimalsOnGround {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Boolean sold;

    @Column(nullable = false)
    private Boolean notSold;

    private Integer order;

    private Boolean deleted;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "AnimalsOnGround{" +
                "id=" + id +
                ", amount=" + amount +
                ", sold=" + sold +
                ", notSold=" + notSold +
                ", order=" + order +
                ", deleted=" + deleted +
                ", category=" + category +
                '}';
    }
}

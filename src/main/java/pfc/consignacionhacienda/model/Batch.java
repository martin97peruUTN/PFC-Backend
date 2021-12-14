package pfc.consignacionhacienda.model;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer corralNumber;

    private String dteNumber;

    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "provenance_id")
    private Provenance provenance;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "batch_id", referencedColumnName ="id")
    @Where(clause = "deleted is null or deleted = false")
    private List<AnimalsOnGround> animalsOnGround;

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

    public String getDteNumber() {
        return dteNumber;
    }

    public void setDteNumber(String dteNumber) {
        this.dteNumber = dteNumber;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Provenance getProvenance() {
        return provenance;
    }

    public void setProvenance(Provenance provenance) {
        this.provenance = provenance;
    }

    public List<AnimalsOnGround> getAnimalsOnGround() {
        return animalsOnGround;
    }

    public void setAnimalsOnGround(List<AnimalsOnGround> animalsOnGround) {
        this.animalsOnGround = animalsOnGround;
    }

    @Override
    public String toString() {
        return "Batch{" +
                "id=" + id +
                ", corralNumber=" + corralNumber +
                ", dteNumber='" + dteNumber + '\'' +
                ", deleted=" + deleted +
                ", auction=" + auction +
                ", provenance=" + provenance +
                ", animalsOnGround=" + animalsOnGround +
                '}';
    }
}

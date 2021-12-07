package pfc.consignacionhacienda.model;

import javax.persistence.*;

@Entity
public class Provenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String reference;
    private String renspaNumber;
    @ManyToOne(optional = false)
    private Locality locality;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRenspaNumber() {
        return renspaNumber;
    }

    public void setRenspaNumber(String renspaNumber) {
        this.renspaNumber = renspaNumber;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    @Override
    public String toString() {
        return "Provenance{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", renspaNumber='" + renspaNumber + '\'' +
                ", locality=" + locality +
                '}';
    }
}

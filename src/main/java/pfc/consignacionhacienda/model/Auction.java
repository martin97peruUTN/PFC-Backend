package pfc.consignacionhacienda.model;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false)
    private String senasaNumber;

    @Column (nullable = false)
    private Instant date;

    @Column
    private Boolean finished;

    @Column
    private Boolean deleted;

    @ManyToOne(optional = false)
    private Locality locality;

    @ManyToMany()
    private List<User> users;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSenasaNumber() {
        return senasaNumber;
    }

    public void setSenasaNumber(String senasaNumber) {
        this.senasaNumber = senasaNumber;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Auction{" +
                "id=" + id +
                ", senasaNumber='" + senasaNumber + '\'' +
                ", date=" + date +
                ", finished=" + finished +
                ", deleted=" + deleted +
                ", locality=" + locality +
                ", users=" + users +
                '}';
    }
}

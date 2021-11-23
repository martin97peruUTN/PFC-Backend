package pfc.consignacionhacienda.dto;

import pfc.consignacionhacienda.model.Locality;
import pfc.consignacionhacienda.model.User;

import java.time.Instant;
import java.util.List;

public class AuctionDTO {

    private Integer id;

    private String senasaNumber;

    private Instant date;

    private Boolean finished;

    private Boolean deleted;

    private Locality locality;

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
}

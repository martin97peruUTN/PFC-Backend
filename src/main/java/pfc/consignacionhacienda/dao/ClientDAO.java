package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Client;

import java.util.List;

@Repository
public interface ClientDAO extends JpaRepository<Client, Integer> {
    @Query("SELECT c FROM CLIENT c WHERE NAME CONTAINS :name AND (DELETED = FALSE OR DELETED IS NULL)")
    List<Client> findByNotDeletedAndName(String name, PageRequest of);
}

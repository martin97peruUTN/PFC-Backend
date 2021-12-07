package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Client;

@Repository
public interface ClientDAO extends JpaRepository<Client, Integer> {
    @Query("SELECT * FROM Client c JOIN c.provenances p WHERE c.name like %:name% AND (c.delete IS NULL OR c.deleted IS FALSE) AND (p.deleted IS NULL OR p.deleted IS FALSE)")
    Page<Client> findByNotDeletedAndName(String name, Pageable of);
}

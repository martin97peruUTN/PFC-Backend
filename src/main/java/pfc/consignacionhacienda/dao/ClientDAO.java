package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Client;
import pfc.consignacionhacienda.model.Provenance;

@Repository
public interface ClientDAO extends JpaRepository<Client, Integer> {

    @Query("SELECT DISTINCT c FROM Client c JOIN c.provenances p WHERE c.name like %:name% AND (c.deleted IS NULL OR c.deleted IS FALSE)")
    Page<Client> findByNotDeletedAndName(String name, Pageable of);

    @Query("SELECT DISTINCT c FROM Client c JOIN c.provenances p WHERE :id=p.id")
    Client findByProvenanceContaining(Integer id);
}

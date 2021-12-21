package pfc.consignacionhacienda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Provenance;
@Repository
public interface ProvenanceDAO extends JpaRepository<Provenance, Integer> {
//    @Query("SELECT c FROM Client c JOIN Provenance c.provenances p WHERE p.id = :provenanceId")
//    Client findClientByProvenanceId(Integer provenanceId);
}

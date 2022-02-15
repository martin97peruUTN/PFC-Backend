package pfc.consignacionhacienda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Provenance;

import java.util.List;

@Repository
public interface ProvenanceDAO extends JpaRepository<Provenance, Integer> {
    @Query(value = "SELECT count(p) > 0 " +
            "FROM Batch b, Provenance p, Auction a " +
            "WHERE p.id = :id " +
            "AND (a.deleted IS NULL OR a.deleted IS FALSE) " +
            "AND (a.finished IS NULL OR a.finished IS FALSE) " +
            "AND (p.deleted IS NULL OR p.deleted IS FALSE) " +
            "AND (b.deleted IS NULL OR b.deleted IS FALSE) " +
            "AND b.provenance.id = p.id AND b.auction.id = a.id")
    boolean isBeingUsed(Integer id);

    @Query(value = "SELECT * FROM provenance p WHERE p.client_id = :id " +
            "AND (p.deleted IS NULL OR p.deleted IS FALSE)", nativeQuery = true)
    List<Provenance> getProvenancesNotDeletedByClientId(Integer id);
//    @Query("SELECT c FROM Client c JOIN Provenance c.provenances p WHERE p.id = :provenanceId")
//    Client findClientByProvenanceId(Integer provenanceId);
}

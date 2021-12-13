package pfc.consignacionhacienda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Provenance;
@Repository
public interface ProvenanceDAO extends JpaRepository<Provenance, Integer> {
}

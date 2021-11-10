package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Locality;

@Repository
public interface LocalityDAO extends JpaRepository<Locality, Integer> {
}

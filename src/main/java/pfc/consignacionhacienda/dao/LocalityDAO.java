package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Locality;

import java.util.List;

@Repository
public interface LocalityDAO extends JpaRepository<Locality, Integer> {
    Page<Locality> findByDeletedNullOrDeletedFalse(Pageable of);

    List<Locality> findByDeletedNullOrDeletedFalse();

    Page<Locality> findByDeletedNullOrDeletedFalseAndNameContaining(Pageable of, String name);
}

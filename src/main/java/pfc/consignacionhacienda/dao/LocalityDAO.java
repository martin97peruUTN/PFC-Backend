package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Locality;

import java.util.List;

@Repository
public interface LocalityDAO extends JpaRepository<Locality, Integer> {
    Page<Locality> findByDeletedNullOrDeletedFalseOrderByName(Pageable of);

    List<Locality> findByDeletedNullOrDeletedFalseOrderByName();

    @Query("Select l from Locality l where (l.name like %:name%) and (l.deleted is null or l.deleted = false) order by l.name")
    Page<Locality> findByName(Pageable of, String name);
}

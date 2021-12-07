package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Client;

@Repository
public interface ClientDAO extends JpaRepository<Client, Integer> {
    @Query("select c from Client c where name like %:name% and (deleted = false or deleted is null)")
    Page<Client> findByNotDeletedAndName(String name, Pageable of);
}

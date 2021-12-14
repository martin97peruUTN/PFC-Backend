package pfc.consignacionhacienda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.SoldBatch;

@Repository
public interface SoldBatchDAO extends JpaRepository<SoldBatch, Integer> {

    @Query("SELECT SUM(b.amount) FROM SoldBatch b JOIN AnimalsOnGround a WHERE a.id=:id")
    Integer getTotalSold(Integer id);
}

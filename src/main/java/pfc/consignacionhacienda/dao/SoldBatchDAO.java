package pfc.consignacionhacienda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.SoldBatch;

import java.util.List;

@Repository
public interface SoldBatchDAO extends JpaRepository<SoldBatch, Integer> {

    @Query("SELECT SUM(b.amount) FROM SoldBatch b JOIN b.animalsOnGround a WHERE a.id=:id AND (b.deleted is null or b.deleted = false)")
    Integer getTotalSold(Integer id);

    @Query("SELECT b FROM SoldBatch b JOIN b.animalsOnGround a WHERE a.id=:id AND (b.deleted is null or b.deleted = false)")
    List<SoldBatch> findSoldBatchesNotDeletedByAnimalsOnGroundId(Integer id);
}

package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT b FROM " +
            "SoldBatch b, Batch ba JOIN b.animalsOnGround a " +
            "JOIN ba.animalsOnGround ag " +
            "JOIN ba.auction au " +
            "WHERE au.id = :auctionId AND ag.id = a.id " +
            "AND (b.deleted is null or b.deleted = false) " +
            "AND (a.deleted is null or a.deleted = false) " +
            "AND (ba.deleted is null or ba.deleted = false)" +
            "AND  (ag.deleted is null or ag.deleted = false)" +
            "AND (au.deleted is null or au.deleted = false)")
    Page<SoldBatch> findByAuctionId(Integer auctionId, Pageable p);
}

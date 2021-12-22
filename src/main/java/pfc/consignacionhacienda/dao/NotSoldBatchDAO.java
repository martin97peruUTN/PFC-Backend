package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.NotSoldBatch;
import pfc.consignacionhacienda.model.SoldBatch;

@Repository
public interface NotSoldBatchDAO extends JpaRepository<NotSoldBatch, Integer> {
    @Query("SELECT nsb FROM " +
            "SoldBatch nsb, Batch ba JOIN nsb.animalsOnGround a " +
            "JOIN ba.animalsOnGround ag " +
            "JOIN ba.auction au " +
            "WHERE au.id = :auctionId AND ag.id = a.id " +
            "AND (nsb.deleted is null or nsb.deleted = false) " +
            "AND (a.deleted is null or a.deleted = false) " +
            "AND (ba.deleted is null or ba.deleted = false)" +
            "AND  (ag.deleted is null or ag.deleted = false)" +
            "AND (au.deleted is null or au.deleted = false)" +
            "AND (au.finished is null or au.finished = false)")
    Page<SoldBatch> findByAuctionId(Integer auctionId, Pageable p);
}

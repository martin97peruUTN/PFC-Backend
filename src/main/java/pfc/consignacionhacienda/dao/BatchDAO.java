package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Batch;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BatchDAO extends JpaRepository<Batch, Integer> {

    @Query("SELECT b FROM Batch b JOIN b.auction a WHERE a.id=:id AND (b.deleted IS NULL OR b.deleted IS FALSE)")
    List<Batch> findByAuctionId(Integer id);

    @Query("SELECT b FROM Batch b JOIN b.auction a WHERE a.id=:id AND (b.deleted IS NULL OR b.deleted IS FALSE)")
    Page<Batch> findByAuctionIdAndPage(Integer id, Pageable of);

    @Query(value = "SELECT DISTINCT b.id, b.deleted, b.corral_number, b.dte_number, b.auction_id, b.provenance_id FROM batch b , animals_on_ground ag WHERE ag.id=:id AND ag.batch_id = b.id AND (ag.deleted IS NULL OR ag.deleted IS FALSE) AND (b.deleted IS NULL OR b.deleted IS FALSE)", nativeQuery = true)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    Batch findByAnimalsOnGroundId(Integer id);

    @Query("SELECT DISTINCT b FROM Batch b, Client c JOIN b.provenance p JOIN b.auction a  JOIN c.provenances pr " +
            "WHERE a.id = :auctionId  " +
            "AND c.id=:id " +
            "AND p.id = pr.id " +
            "AND (b.deleted IS NULL OR b.deleted IS FALSE)")
    List<Batch> findByClientIdAndAuctionId(Integer id, Integer auctionId);
}

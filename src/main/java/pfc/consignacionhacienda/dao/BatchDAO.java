package pfc.consignacionhacienda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Batch;

import java.util.List;

@Repository
public interface BatchDAO extends JpaRepository<Batch, Integer> {

    @Query("SELECT b FROM Batch b JOIN b.auction a WHERE a.id=:id AND (b.deleted IS NULL OR b.deleted IS FALSE)")
    List<Batch> findByAuctionId(Integer id);

    Batch findByAnimalsOnGroundId(Integer id);
}

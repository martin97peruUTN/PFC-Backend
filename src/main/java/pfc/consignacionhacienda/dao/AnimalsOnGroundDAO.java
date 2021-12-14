package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.AnimalsOnGround;

@Repository
public interface AnimalsOnGroundDAO extends JpaRepository<AnimalsOnGround, Integer> {

    @Query("SELECT ag FROM Batch b JOIN b.animalsOnGround ag JOIN b.auction au WHERE au.id=:auctionId AND (ag.deleted IS NULL OR ag.deleted IS FALSE) AND (b.deleted IS NULL OR b.deleted IS FALSE) AND (au.deleted IS NULL OR au.deleted IS FALSE)")
    Page<AnimalsOnGround> getAnimalsOnGroundByAuction(Integer auctionId, Pageable of);

    @Query("SELECT ag FROM Batch b JOIN b.animalsOnGround ag JOIN b.auction au WHERE au.id=:auctionId AND ag.sold IS TRUE AND (ag.deleted IS NULL OR ag.deleted IS FALSE) AND (b.deleted IS NULL OR b.deleted IS FALSE) AND (au.deleted IS NULL OR au.deleted IS FALSE)")
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionSold(Integer auctionId, Pageable of);

    @Query("SELECT ag FROM Batch b JOIN b.animalsOnGround ag JOIN b.auction au WHERE au.id=:auctionId AND ag.sold IS FALSE AND ag.notSold IS TRUE AND (ag.deleted IS NULL OR ag.deleted IS FALSE) AND (b.deleted IS NULL OR b.deleted IS FALSE) AND (au.deleted IS NULL OR au.deleted IS FALSE)")
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionNotSold(Integer auctionId, Pageable of);

    @Query("SELECT ag FROM Batch b JOIN b.animalsOnGround ag JOIN b.auction au WHERE au.id=:auctionId AND ag.sold IS FALSE AND ag.notSold IS FALSE AND (ag.deleted IS NULL OR ag.deleted IS FALSE) AND (b.deleted IS NULL OR b.deleted IS FALSE) AND (au.deleted IS NULL OR au.deleted IS FALSE)")
    Page<AnimalsOnGround> getAnimalsOnGroundByAuctionForSell(Integer auctionId, Pageable of);
}

package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.AnimalsOnGround;

@Repository
public interface AnimalsOnGroundDAO extends JpaRepository<AnimalsOnGround, Integer> {

    @Query("SELECT ag FROM AnimalsOnGround ag JOIN Batch b JOIN Auction au WHERE au.id=:auctionId")
    Page<AnimalsOnGround> getAnimalsOnGroundByAuction(Integer auctionId, Pageable of);

}

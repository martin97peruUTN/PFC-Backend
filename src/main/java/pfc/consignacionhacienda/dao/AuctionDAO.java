package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Auction;

import java.util.List;

@Repository
public interface AuctionDAO extends JpaRepository<Auction, Integer> {
    Page<Auction> findByDeletedNullOrDeletedFalse(Pageable of);

    List<Auction> findByDeletedNullOrDeletedFalse();
}

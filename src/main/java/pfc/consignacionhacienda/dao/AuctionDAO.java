package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Auction;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionDAO extends JpaRepository<Auction, Integer> {
    Page<Auction> findByDeletedNullOrDeletedFalse(Pageable of);

    List<Auction> findByDeletedNullOrDeletedFalse();

    @Query(value = ("SELECT a FROM Auction a JOIN a.users u WHERE u.id = :userId " +
            "AND (a.finished IS NULL OR a.finished IS FALSE) " +
            "AND (a.deleted IS NULL OR a.deleted IS FALSE) " +
            "ORDER BY a.date ASC"))
    Page<Auction> findOwnById(Integer userId, Pageable of);

    @Query(value = ("SELECT a FROM Auction a JOIN a.users u WHERE u.id != :userId " +
            "AND (a.finished IS NULL OR a.finished IS FALSE) " +
            "AND (a.deleted IS NULL OR a.deleted IS FALSE) " +
            "ORDER BY a.date ASC"))
    Page<Auction> findOthersById(Integer userId, Pageable of);

    @Query(value = ("SELECT a FROM Auction a WHERE " +
            "(a.finished IS NULL OR a.finished IS FALSE) " +
            "AND (a.deleted IS NULL OR a.deleted IS FALSE) " +
            "ORDER BY a.date ASC"))
    Page<Auction> findAllAdmin(Pageable of);

    Optional<Auction> findByIdAndDeletedNullOrDeletedFalse(Integer id);

    @Query(value = ("SELECT a FROM Auction a JOIN a.users u WHERE " +
            " u.id = :userId " +
            "AND a.finished IS TRUE" +
            " AND a.date BETWEEN :since AND :until " +
            "AND (a.deleted IS NULL OR a.deleted IS FALSE) " +
            "ORDER BY a.date DESC"))
    Page<Auction> findByFinishedAndBetween(Integer userId, Instant since, Instant until, Pageable of);

    @Query(value = ("SELECT a FROM Auction a WHERE " +
            " a.finished IS TRUE " +
            " AND a.date BETWEEN :since AND :until " +
            "AND (a.deleted IS NULL OR a.deleted IS FALSE) " +
            "ORDER BY a.date DESC"))
    Page<Auction> findByFinishedAndBetween(Instant since, Instant until, Pageable of);
}

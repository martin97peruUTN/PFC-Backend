package pfc.consignacionhacienda.services.soldBatch;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.dto.SoldBatchDTO;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.soldBatch.SoldBatchNotFoundException;
import pfc.consignacionhacienda.model.SoldBatch;

import java.util.List;

public interface SoldBatchService {
    Integer getTotalSold(Integer animalsOnGroundId);

    List<SoldBatch> findSoldBatchesNotDeletedByAnimalsOnGroundId(Integer id);
    SoldBatch saveSoldBatch(SoldBatch soldBatch, Integer animalsOnGroundId) throws AnimalsOnGroundNotFound, HttpForbidenException, BatchNotFoundException, AuctionNotFoundException, HttpUnauthorizedException;
    SoldBatch updateSoldBatchById(SoldBatchDTO soldBatchDTO, Integer soldBatchId) throws SoldBatchNotFoundException, AnimalsOnGroundNotFound, BatchNotFoundException, AuctionNotFoundException, HttpForbidenException, HttpUnauthorizedException;
    Page<SoldBatchResponseDTO> getSoldBatchesByAuctionAndPage(Integer auctionId, Integer page, Integer limit);
    SoldBatch deleteById(Integer soldBatchId) throws HttpUnauthorizedException, AnimalsOnGroundNotFound, SoldBatchNotFoundException, AuctionNotFoundException, HttpForbidenException, BatchNotFoundException;

    SoldBatch findSoldBatchById(Integer soldBatchId) throws SoldBatchNotFoundException;

    List<SoldBatchResponseDTO> getAllSoldBatchesByAuctionId(Integer auctionId);
}

package pfc.consignacionhacienda.services.soldBatch;

import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.model.SoldBatch;

import java.util.List;

public interface SoldBatchService {
    Integer getTotalSold(Integer id);

    List<SoldBatch> findSoldBatchesNotDeletedByAnimalsOnGroundId(Integer id);
    SoldBatch saveSoldBatch(SoldBatch soldBatch, Integer animalsOnGroundId) throws AnimalsOnGroundNotFound, HttpForbidenException, BatchNotFoundException, AuctionNotFoundException, HttpUnauthorizedException;
}

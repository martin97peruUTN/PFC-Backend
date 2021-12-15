package pfc.consignacionhacienda.services.batch;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Batch;

public interface BatchService {
    Page<AnimalsOnGroundDTO> getAnimalListDTO(Integer auctionId, Boolean sold, Boolean notSold, Integer page, Integer limit) throws AuctionNotFoundException;
    Batch getBatchByAnimalsOnGroundId(Integer animalsOnGroundId);
    Batch saveBatch(Batch newBatch, Integer auctionId) throws AuctionNotFoundException, HttpForbidenException;
    AnimalsOnGround addAnimalsOnGround(Integer batchId, AnimalsOnGround animalsOnGround) throws IllegalArgumentException, BatchNotFoundException, AuctionNotFoundException, HttpForbidenException;
    Batch findById(Integer batchId) throws BatchNotFoundException;
}

package pfc.consignacionhacienda.services.batch;

import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.model.Batch;

import java.util.List;

public interface BatchService {
    List<AnimalsOnGroundDTO> getAnimalListDTO(Integer auctionId, Boolean sold, Boolean notSold, Integer page, Integer limit);
    List<Batch> getBatchesByAuctionId(Integer auctionId);
    Batch getBatchByAnimalsOnGroundId(Integer animalsOnGroundId);
}

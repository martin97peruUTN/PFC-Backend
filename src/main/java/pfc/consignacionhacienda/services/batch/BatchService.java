package pfc.consignacionhacienda.services.batch;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.model.Batch;

import java.util.List;

public interface BatchService {
    Page<AnimalsOnGroundDTO> getAnimalListDTO(Integer auctionId, Boolean sold, Boolean notSold, Integer page, Integer limit) throws AuctionNotFoundException;
    Batch getBatchByAnimalsOnGroundId(Integer animalsOnGroundId);
}

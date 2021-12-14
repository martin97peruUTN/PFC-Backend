package pfc.consignacionhacienda.services.animalsOnGround;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Batch;

public interface AnimalsOnGroundService {
    Page<AnimalsOnGround> getAnimalsOnGroundByAuction(Integer auctionId, Pageable of);
}

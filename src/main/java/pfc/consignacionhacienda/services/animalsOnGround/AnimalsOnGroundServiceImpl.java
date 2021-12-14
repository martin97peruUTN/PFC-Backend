package pfc.consignacionhacienda.services.animalsOnGround;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.AnimalsOnGroundDAO;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Batch;
import pfc.consignacionhacienda.services.batch.BatchService;

@Service
public class AnimalsOnGroundServiceImpl implements AnimalsOnGroundService{

    @Autowired
    AnimalsOnGroundDAO animalsOnGroundDAO;

    @Override
    public Page<AnimalsOnGround> getAnimalsOnGroundByAuction(Integer auctionId, Pageable of) {
        return animalsOnGroundDAO.getAnimalsOnGroundByAuction(auctionId, of);
    }
}

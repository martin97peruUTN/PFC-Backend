package pfc.consignacionhacienda.services.animalsOnGround;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.AnimalsOnGroundDAO;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.model.AnimalsOnGround;

import java.util.Optional;

@Service
public class AnimalsOnGroundServiceImpl implements AnimalsOnGroundService{

    @Autowired
    AnimalsOnGroundDAO animalsOnGroundDAO;

    @Override
    public Page<AnimalsOnGround> getAnimalsOnGroundByAuction(Integer auctionId, Pageable of) {
        return animalsOnGroundDAO.getAnimalsOnGroundByAuction(auctionId, of);
    }

    @Override
    public Page<AnimalsOnGround> getAnimalsOnGroundByAuctionSold(Integer auctionId, Pageable of) {
        return animalsOnGroundDAO.getAnimalsOnGroundByAuctionSold(auctionId, of);
    }

    @Override
    public Page<AnimalsOnGround> getAnimalsOnGroundByAuctionNotSold(Integer auctionId, Pageable of) {
        return animalsOnGroundDAO.getAnimalsOnGroundByAuctionNotSold(auctionId, of);
    }

    @Override
    public Page<AnimalsOnGround> getAnimalsOnGroundByAuctionForSell(Integer auctionId, Pageable of) {
        return animalsOnGroundDAO.getAnimalsOnGroundByAuctionForSell(auctionId, of);
    }

    @Override
    public AnimalsOnGround deleteById(Integer id) throws AnimalsOnGroundNotFound {
        AnimalsOnGround animalsOnGround = findById(id);
        if(animalsOnGround.getDeleted() != null && animalsOnGround.getDeleted()){
            throw new AnimalsOnGroundNotFound("El conjunto de animales en pista con id: " + id + " no existe");
        }
        animalsOnGround.setDeleted(true);
        return animalsOnGroundDAO.save(animalsOnGround);
    }

    private AnimalsOnGround findById(Integer id) throws AnimalsOnGroundNotFound {
        Optional<AnimalsOnGround> animalsOnGround = animalsOnGroundDAO.findById(id);
        if(animalsOnGround.isPresent()){
            return animalsOnGround.get();
        }
        throw new AnimalsOnGroundNotFound("El conjunto de animales en pista con id: " + id + " no existe");
    }
}

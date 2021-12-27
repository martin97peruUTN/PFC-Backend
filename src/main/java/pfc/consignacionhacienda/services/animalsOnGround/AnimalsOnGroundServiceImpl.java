package pfc.consignacionhacienda.services.animalsOnGround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.AnimalsOnGroundDAO;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;
import pfc.consignacionhacienda.utils.AnimalsOnGoundMapper;

import java.util.Optional;

@Service
public class AnimalsOnGroundServiceImpl implements AnimalsOnGroundService{

    private static final Logger logger = LoggerFactory.getLogger(AnimalsOnGroundServiceImpl.class);

    @Autowired
    AnimalsOnGroundDAO animalsOnGroundDAO;

    @Autowired
    BatchService batchService;

    @Autowired
    SoldBatchService soldBatchService;

    @Autowired
    AnimalsOnGoundMapper animalsOnGoundMapper;

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
        logger.debug(animalsOnGround.toString());
        if(animalsOnGround.getDeleted() != null && animalsOnGround.getDeleted()){
            throw new AnimalsOnGroundNotFound("El conjunto de animales en pista con id: " + id + " no existe");
        }
        animalsOnGround.setDeleted(true);
        return animalsOnGroundDAO.save(animalsOnGround);
    }

    @Override
    public AnimalsOnGround getAnimalsOnGroundNotDeletedById(Integer animalsId) {
        return animalsOnGroundDAO.findByIdAndNotDeleted(animalsId);
    }

    @Override
    public AnimalsOnGround updateAnimalsOnGround(Integer animalsId, AnimalsOnGroundDTO animalsOnGroundDTO) throws BadHttpRequest, IllegalArgumentException,AnimalsOnGroundNotFound, AuctionNotFoundException, HttpForbidenException {
        if(animalsOnGroundDTO.getId() != null && !animalsOnGroundDTO.getId().equals(animalsId)){
            throw new BadHttpRequest("El id del path no coincide con el id del body del request");
        }

        if(animalsOnGroundDTO.getAmount() != null && animalsOnGroundDTO.getAmount() <= 0){
            throw new IllegalArgumentException("La cantidad de animales debe ser mayor a cero");
        }
        AnimalsOnGround animalsOnGround = getAnimalsOnGroundNotDeletedById(animalsId);
        if(animalsOnGround == null){
            throw new AnimalsOnGroundNotFound("El conjunto de animales en pista con id: " + animalsId + " no existe");
        }
        Auction auction = batchService.getBatchByAnimalsOnGroundId(animalsId).getAuction();
        if(auction.getDeleted()!=null && auction.getDeleted()){
            throw new AuctionNotFoundException("El conjunto de Animales En Pista pertenece a un remate inexistente.");
        }
        if(auction.getFinished()!=null && auction.getFinished()){
            throw new HttpForbidenException("No se pueden editar Animales En Pista de un remate que ya se ha realizado");
        }

        Integer totalVendidos = soldBatchService.getTotalSold(animalsId);
        if(animalsOnGroundDTO.getAmount() != null && totalVendidos > animalsOnGroundDTO.getAmount()){
            throw new HttpForbidenException("La cantidad 'amount' de Animales En Pista no puede ser menor a la cantidad que ya se ha vendido.");
        }
        if(animalsOnGroundDTO.getAmount() != null && animalsOnGround.getAmount() < animalsOnGroundDTO.getAmount() && animalsOnGround.getSold() != null && animalsOnGround.getSold()){
            animalsOnGround.setSold(false);
        }
        animalsOnGroundDTO.setSold(null);
        logger.info(animalsOnGroundDTO.toString());
        animalsOnGoundMapper.updateAnimalsOnGroundFromDto(animalsOnGroundDTO, animalsOnGround);
        logger.info(animalsOnGround.toString());
        return animalsOnGroundDAO.save(animalsOnGround);
    }

    public AnimalsOnGround findById(Integer id) throws AnimalsOnGroundNotFound {
        Optional<AnimalsOnGround> animalsOnGround = animalsOnGroundDAO.findById(id);
        if(animalsOnGround.isPresent()){
            return animalsOnGround.get();
        }
        throw new AnimalsOnGroundNotFound("El conjunto de animales en pista con id: " + id + " no existe");
    }

    @Override
    public AnimalsOnGround findByIdNotDeleted(Integer animalsOnGroundId) throws AnimalsOnGroundNotFound {
        AnimalsOnGround animalsOnGround = this.findById(animalsOnGroundId);
        if(animalsOnGround.getDeleted() != null && animalsOnGround.getDeleted()){
            throw new AnimalsOnGroundNotFound("El conjunto de animales en pista con id: " + animalsOnGroundId + " no existe");
        }
        return animalsOnGround;
    }

    @Override
    public AnimalsOnGround save(AnimalsOnGround animalsOnGround) {
        return animalsOnGroundDAO.save(animalsOnGround);
    }
}

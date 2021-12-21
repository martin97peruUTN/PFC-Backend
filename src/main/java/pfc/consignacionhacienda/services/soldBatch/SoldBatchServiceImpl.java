package pfc.consignacionhacienda.services.soldBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.SoldBatchDAO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.model.*;
import pfc.consignacionhacienda.services.animalsOnGround.AnimalsOnGroundService;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.user.UserService;

import java.util.List;

@Service
public class SoldBatchServiceImpl implements SoldBatchService{

    private static final Logger logger = LoggerFactory.getLogger(SoldBatchServiceImpl.class);

    @Autowired
    private SoldBatchDAO soldBatchDAO;

    @Autowired
    private AnimalsOnGroundService animalsOnGroundService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private BatchService batchService;

    @Override
    public Integer getTotalSold(Integer id) {
        Integer total = soldBatchDAO.getTotalSold(id);
        return total==null?0:total;
    }

    @Override
    public List<SoldBatch> findSoldBatchesNotDeletedByAnimalsOnGroundId(Integer id) {
        return soldBatchDAO.findSoldBatchesNotDeletedByAnimalsOnGroundId(id);
    }

    @Override
    public SoldBatch saveSoldBatch(SoldBatch soldBatch, Integer animalsOnGroundId) throws AnimalsOnGroundNotFound, HttpForbidenException, BatchNotFoundException, AuctionNotFoundException, HttpUnauthorizedException {
        AnimalsOnGround animalsOnGround = animalsOnGroundService.findByIdNotDeleted(animalsOnGroundId);
        Integer totalSold = this.getTotalSold(animalsOnGroundId);
        if(soldBatch.getAmount() == null){
            throw new IllegalArgumentException("El atributo 'cantidad' no puede ser nulo");
        }
        Batch batch = batchService.getBatchByAnimalsOnGroundId(animalsOnGroundId);
        if(batch == null || (batch.getDeleted() != null && batch.getDeleted())){
            throw new BatchNotFoundException("El Lote Vendido pertenece a un Lote de Venta inexistente");
        }
        Auction auction = batch.getAuction();
        if(auction.getDeleted() != null && auction.getDeleted()){
            throw new AuctionNotFoundException("El Lote Vendido pertenece a un Remate inexistente");
        }
        if(soldBatch.getAmount()+totalSold > animalsOnGround.getAmount()){
            throw new HttpForbidenException("La cantidad de animales vendidos supera a la cantidad disponible para la venta.");
        }
        if(auction.getFinished() != null && auction.getFinished()){
            throw new HttpForbidenException("No puede editarse un remate eliminado.");
        }
        boolean userBelongsToAuction = auction.getUsers().stream().anyMatch(u -> u.getId().equals(userService.getCurrentUser().getId()));

        if(!userBelongsToAuction){
            throw new HttpUnauthorizedException("Usted no esta autorizado a editar este remate.");
        }
        soldBatch.setAnimalsOnGround(animalsOnGround);
        SoldBatch soldBatchSaved = soldBatchDAO.save(soldBatch);
        totalSold = this.getTotalSold(animalsOnGroundId);
        if(totalSold.equals(animalsOnGround.getAmount())){
            animalsOnGround.setSold(true);
            animalsOnGroundService.save(animalsOnGround);
        }
        return soldBatchSaved;
    }
}

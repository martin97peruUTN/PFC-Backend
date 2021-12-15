package pfc.consignacionhacienda.services.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.BatchDAO;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.Batch;
import pfc.consignacionhacienda.services.animalsOnGround.AnimalsOnGroundService;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class BatchServiceImpl implements BatchService{

    private static final Logger logger = LoggerFactory.getLogger(BatchServiceImpl.class);
    @Autowired
    private BatchDAO batchDAO;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SoldBatchService soldBatchService;

    @Autowired
    private AnimalsOnGroundService animalsOnGroundService;

    @Autowired
    private AuctionService auctionService;

    @Override
    public Page<AnimalsOnGroundDTO> getAnimalListDTO(Integer auctionId, Boolean sold, Boolean notSold, Integer page, Integer limit) throws AuctionNotFoundException {
        Auction a = auctionService.getAuctionById(auctionId);
        if(a.getDeleted() != null && a.getDeleted()){
            throw new AuctionNotFoundException("El remate con id: " + auctionId + " no existe");
        }
        ArrayList<AnimalsOnGroundDTO> animalList = new ArrayList<>();
        Page<AnimalsOnGround> animalsOnGroundPage;
        Pageable p = PageRequest.of(page, limit);
        if(sold){
            animalsOnGroundPage = animalsOnGroundService.getAnimalsOnGroundByAuctionSold(auctionId, p);
        }else{
            if(notSold){
                animalsOnGroundPage = animalsOnGroundService.getAnimalsOnGroundByAuctionNotSold(auctionId, p);
            }else{
                animalsOnGroundPage = animalsOnGroundService.getAnimalsOnGroundByAuctionForSell(auctionId, p);
            }
        }
        for(AnimalsOnGround animalsOnGround: animalsOnGroundPage){
            AnimalsOnGroundDTO newAnimalDTO = new AnimalsOnGroundDTO();
            Batch batch = getBatchByAnimalsOnGroundId(animalsOnGround.getId());
            newAnimalDTO.setId(animalsOnGround.getId());
            newAnimalDTO.setAmount(animalsOnGround.getAmount());
            newAnimalDTO.setCategory(animalsOnGround.getCategory());
            newAnimalDTO.setCorralNumber(batch.getCorralNumber());
            newAnimalDTO.setSeller(clientService.findByProvenanceId(batch.getProvenance().getId()));
            newAnimalDTO.setSoldAmount(soldBatchService.getTotalSold(newAnimalDTO.getId()));
            animalList.add(newAnimalDTO);
        }
        return new PageImpl<>(animalList, p, animalsOnGroundPage.getTotalElements());
    }

    @Override
    public Batch getBatchByAnimalsOnGroundId(Integer animalsOnGroundId) {
        return batchDAO.findByAnimalsOnGroundId(animalsOnGroundId);
    }

    @Override
    public Batch saveBatch(Batch newBatch, Integer auctionId) throws AuctionNotFoundException, IllegalArgumentException, HttpForbidenException {
        Auction auction = getAuctionNotDeletedById(auctionId);
        if(newBatch.getAnimalsOnGround() == null || newBatch.getAnimalsOnGround().isEmpty()){
            throw new IllegalArgumentException("El lote debe tener al menos un conjunto de animales en pista.");
        }
        newBatch.setAuction(auction);
        return batchDAO.save(newBatch);
    }

    @Override
    public Batch findById(Integer batchId) throws BatchNotFoundException{
        Optional<Batch> batchOptional = batchDAO.findById(batchId);
        if(batchOptional.isPresent()){
            return batchOptional.get();
        }
        throw new BatchNotFoundException("El lote con id: " + batchId + " no existe.");
    }
    @Override
    public AnimalsOnGround addAnimalsOnGround(Integer batchId, AnimalsOnGround animalsOnGround) throws BatchNotFoundException, IllegalArgumentException, AuctionNotFoundException, HttpForbidenException {
        Batch batch = findById(batchId);
        if(batch.getDeleted() != null && batch.getDeleted()){
            throw new BatchNotFoundException("El lote con id: " + batchId + " no existe.");
        }
        if(batch.getAuction().getDeleted() != null && batch.getAuction().getDeleted()){
            throw new AuctionNotFoundException("El remate fue eliminado.");
        }
        if(batch.getAuction().getFinished() != null && batch.getAuction().getDeleted()){
            throw new HttpForbidenException("No se pueden editar lotes de un remate finalizado.");
        }
        if(animalsOnGround.getAmount() < 0){
            throw new IllegalArgumentException("La cantidad de animales a agregar debe ser mayor a 0");
        }
        batch.getAnimalsOnGround().add(animalsOnGround);
        logger.debug(batch.getAnimalsOnGround().toString());
        logger.debug(animalsOnGround.toString());
        batchDAO.save(batch);
        return animalsOnGround;
    }

    private Auction getAuctionNotDeletedById(Integer auctionId) throws AuctionNotFoundException, HttpForbidenException {
        Auction auction = auctionService.getAuctionById(auctionId);
        if (auction.getDeleted()) {
            throw new AuctionNotFoundException("El remate con id: " + auctionId + " no existe.");
        }
        if (auction.getFinished()) {
            throw new HttpForbidenException("El remate ya ha finalizado, por lo tanto, no puede agregarse este lote al mismo.");
        }
        return auction;
    }
}

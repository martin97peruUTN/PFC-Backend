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
import pfc.consignacionhacienda.dto.BatchDTO;
import pfc.consignacionhacienda.dto.BatchWithClientDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.*;
import pfc.consignacionhacienda.services.animalsOnGround.AnimalsOnGroundService;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;
import pfc.consignacionhacienda.utils.AnimalsOnGoundMapper;
import pfc.consignacionhacienda.utils.BatchMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BatchServiceImpl implements BatchService{

    private static final Logger logger = LoggerFactory.getLogger(BatchServiceImpl.class);

    @Autowired
    private BatchDAO batchDAO;

    @Autowired
    private BatchMapper batchMapper;

    @Autowired
    private AnimalsOnGoundMapper animalsOnGoundMapper;

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
        logger.debug(auction.toString());
        if(newBatch.getAnimalsOnGround() == null || newBatch.getAnimalsOnGround().isEmpty()){
            throw new IllegalArgumentException("El lote debe tener al menos un conjunto de animales en pista.");
        }
        newBatch.setAuction(auction);
        logger.debug(newBatch.toString());
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
    public BatchWithClientDTO getBatchByAnimalsOnGroundIdWithClient(Integer animalsOnGroundId) throws BatchNotFoundException, ClientNotFoundException, AnimalsOnGroundNotFound {
        AnimalsOnGround animalsOnGround = animalsOnGroundService.findById(animalsOnGroundId);
        if(animalsOnGround.getDeleted() != null && animalsOnGround.getDeleted()){
            throw new AnimalsOnGroundNotFound("EL conjunto de Animales en Pista con id " + animalsOnGroundId + " no existe");
        }
        Batch batch = getBatchByAnimalsOnGroundId(animalsOnGroundId);
        logger.debug(batch.toString());
        if(batch.getDeleted() != null && batch.getDeleted()){
            throw new BatchNotFoundException("El conjunto de animales en pista con id " + animalsOnGroundId + " pertenece a un lote de animales inexistente");
        }
        Client c = clientService.findByProvenanceId(batch.getProvenance().getId());
        BatchWithClientDTO batchWithClientDTO = new BatchWithClientDTO();
        batchWithClientDTO.setAnimalsOnGround(batch.getAnimalsOnGround());
        batchWithClientDTO.setProvenance(batch.getProvenance());
        batchWithClientDTO.setCorralNumber(batch.getCorralNumber());
        batchWithClientDTO.setDteNumber(batch.getDteNumber());
        batchWithClientDTO.setId(batch.getId());
        logger.debug(c.toString());
        batchWithClientDTO.setClient(c);

        return batchWithClientDTO;
    }

    @Override
    public AnimalsOnGround deleteAnimalsOnGroundById(Integer animalsId) throws AnimalsOnGroundNotFound, HttpForbidenException, AuctionNotFoundException, BatchNotFoundException {
        Batch batchOwn = getBatchByAnimalsOnGroundId(animalsId);
        if(batchOwn == null){
            throw new AnimalsOnGroundNotFound("El conjunto de Animales En Pista co id " + animalsId + " no existe");
        }
        if(batchOwn.getDeleted() != null && batchOwn.getDeleted()){
            throw new BatchNotFoundException("El lote al que pertenece estos animales no existe");
        }
        if(batchOwn.getAuction().getDeleted() != null && batchOwn.getAuction().getDeleted()){
            throw new AuctionNotFoundException("El remate al que pertenece estos animales no existe");
        }
        if(batchOwn.getAuction().getFinished() != null && batchOwn.getAuction().getFinished()){
            throw new HttpForbidenException("No puede modificarse un remate que ya se ha realizado");
        }
        return animalsOnGroundService.deleteById(animalsId);
    }

    @Override
    public Batch updateBatchById(Integer batchId, BatchDTO batchDTO) throws IllegalArgumentException, BatchNotFoundException, BadHttpRequest, AuctionNotFoundException, HttpForbidenException {
        if(batchDTO.getId() != null && !batchDTO.getId().equals(batchId)){
            throw new BadHttpRequest("El id del path no coincide con el id del body del request");
        }
        Batch batch = findById(batchId);
        if(batch.getAuction().getDeleted() != null && batch.getAuction().getDeleted()){
            throw new AuctionNotFoundException("El lote pertenece a un remate que no existe");
        }
        if(batch.getAuction().getFinished() != null && batch.getAuction().getFinished()){
            throw new HttpForbidenException("No se puede editar un lote de un remate que ya se ha realizado.");
        }
        if(batch.getDeleted() != null && batch.getDeleted()){
            throw new BatchNotFoundException("El lote con id: " + batchId + " no existe.");
        }
        batchMapper.updateBatchFromDto(batchDTO, batch);
        return batchDAO.save(batch);
    }

    @Override
    public AnimalsOnGround updateAnimalsOnGroundById(Integer animalsId, AnimalsOnGroundDTO animalsOnGroundDTO) throws BadHttpRequest,IllegalArgumentException, AnimalsOnGroundNotFound, HttpForbidenException, AuctionNotFoundException {
        return animalsOnGroundService.updateAnimalsOnGround(animalsId,animalsOnGroundDTO );
    }

    @Override
    public List<AnimalsOnGround> addAnimalsOnGround(Integer batchId, AnimalsOnGround animalsOnGround) throws BatchNotFoundException, IllegalArgumentException, AuctionNotFoundException, HttpForbidenException {
        Batch batch = findById(batchId);
        if(batch.getDeleted() != null && batch.getDeleted()){
            throw new BatchNotFoundException("El lote con id: " + batchId + " no existe.");
        }
        if(batch.getAuction().getDeleted() != null && batch.getAuction().getDeleted()){
            throw new AuctionNotFoundException("El remate fue eliminado.");
        }
        if(batch.getAuction().getFinished() != null && batch.getAuction().getFinished()){
            throw new HttpForbidenException("No se pueden editar lotes de un remate finalizado.");
        }
        if(animalsOnGround.getAmount() <= 0){
            throw new IllegalArgumentException("La cantidad de animales a agregar debe ser mayor a 0");
        }
        batch.getAnimalsOnGround().add(animalsOnGround);
        logger.debug(batch.getAnimalsOnGround().toString());
        logger.debug(animalsOnGround.toString());
        Batch updatedBatch = batchDAO.save(batch);
        return updatedBatch.getAnimalsOnGround();
    }

    private Auction getAuctionNotDeletedById(Integer auctionId) throws AuctionNotFoundException, HttpForbidenException {
        Auction auction = auctionService.getAuctionById(auctionId);
        if (auction.getDeleted() != null && auction.getDeleted()) {
            throw new AuctionNotFoundException("El remate con id: " + auctionId + " no existe.");
        }
        if (auction.getFinished() != null && auction.getFinished()) {
            throw new HttpForbidenException("El remate ya ha finalizado, por lo tanto, no puede agregarse este lote al mismo.");
        }
        return auction;
    }

    @Override
    public Batch deleteBatchById(Integer batchId) throws HttpForbidenException, AuctionNotFoundException, BatchNotFoundException {
        Batch batch = this.findById(batchId);
        if(batch.getDeleted() != null && batch.getDeleted()){
            throw new BatchNotFoundException("El lote con id: "+ batchId + " no existe");
        }
        Auction auction = batch.getAuction();
        if(auction.getDeleted() != null && auction.getDeleted()){
            throw new AuctionNotFoundException("El lote pertenece a un remate inexistente.");
        }
        if(auction.getFinished() != null && auction.getFinished()){
            throw new HttpForbidenException("No se puede modificar lotes de un remate que ya se ha realizado");
        }
        boolean canBeDeleted = true;
        List<AnimalsOnGround> animalsOnGroundList = batch.getAnimalsOnGround();
        if(animalsOnGroundList != null && !animalsOnGroundList.isEmpty()) {
            for (AnimalsOnGround animalsOnGround : animalsOnGroundList) {
                List<SoldBatch> soldBatches = soldBatchService.findSoldBatchesNotDeletedByAnimalsOnGroundId(animalsOnGround.getId());
                if(soldBatches != null && soldBatches.size() > 0){
                    canBeDeleted = false;
                }
            }
        }
        if(!canBeDeleted){
            throw new HttpForbidenException("No puede eliminarse un lote de animales que ya tiene animales vendidos.");
        }
        batch.setDeleted(true);
        return batchDAO.save(batch);
    }

    @Override
    public List<Batch> getBatchesByAuctionId(Integer id) {
        return batchDAO.findByAuctionId(id);
    }

    @Override
    public List<AnimalsOnGround> sortAnimalsOnGround(List<AnimalsOnGroundDTO> animalsOnGroundDTOList, Integer auctionId) throws IllegalArgumentException, AnimalsOnGroundNotFound {
        return animalsOnGroundService.sortAnimalsOnGround(animalsOnGroundDTOList, auctionId);
    }

}

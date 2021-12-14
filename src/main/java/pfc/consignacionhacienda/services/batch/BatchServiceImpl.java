package pfc.consignacionhacienda.services.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.BatchDAO;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.Batch;
import pfc.consignacionhacienda.services.animalsOnGround.AnimalsOnGroundService;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;

import java.util.ArrayList;

@Service
public class BatchServiceImpl implements BatchService{

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
    public Page getAnimalListDTO(Integer auctionId, Boolean sold, Boolean notSold, Integer page, Integer limit) throws AuctionNotFoundException {
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
}

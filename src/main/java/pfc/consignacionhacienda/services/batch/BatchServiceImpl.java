package pfc.consignacionhacienda.services.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.BatchDAO;
import pfc.consignacionhacienda.dto.AnimalsOnGroundDTO;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Batch;
import pfc.consignacionhacienda.model.Client;
import pfc.consignacionhacienda.services.animalsOnGround.AnimalsOnGroundService;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchServiceImpl implements BatchService{

    @Autowired
    BatchDAO batchDAO;

    @Autowired
    ClientService clientService;

    @Autowired
    SoldBatchService soldBatchService;

    @Autowired
    AnimalsOnGroundService animalsOnGroundService;

    @Override
    public List<AnimalsOnGroundDTO> getAnimalListDTO(Integer auctionId, Boolean sold, Boolean notSold, Integer page, Integer limit) {

        List<AnimalsOnGroundDTO> animalList = new ArrayList<>();
        Pageable p = PageRequest.of(page, limit);
        Page<AnimalsOnGround> animalsOnGroundPage = animalsOnGroundService.getAnimalsOnGroundByAuction(auctionId, p);
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
        return animalList;
    }

    @Override
    public List<Batch> getBatchesByAuctionId(Integer auctionId) {
        return batchDAO.findByAuctionId(auctionId);
    }

    @Override
    public Batch getBatchByAnimalsOnGroundId(Integer animalsOnGroundId) {
        return batchDAO.findByAnimalsOnGroundId(animalsOnGroundId);
    }
}

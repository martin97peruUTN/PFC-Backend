package pfc.consignacionhacienda.services.notSoldBatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.NotSoldBatchDAO;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.model.SoldBatch;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.client.ClientService;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotSoldBatchServiceImpl implements NotSoldBatchService{
    @Autowired
    private NotSoldBatchDAO notSoldBatchDAO;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BatchService batchService;

    @Override
    public Page<SoldBatchResponseDTO> getNotSoldBatchesByAuctionAndPage(Integer auctionId, Integer page, Integer limit) {
        Pageable p = PageRequest.of(page, limit);
        Page<SoldBatch> soldBatches = notSoldBatchDAO.findByAuctionId(auctionId, p);
        List<SoldBatchResponseDTO> responseDTOList = new ArrayList<>();
        for(SoldBatch soldBatch: soldBatches){
            SoldBatchResponseDTO soldBatchResponseDTO = new SoldBatchResponseDTO();
            soldBatchResponseDTO.setAmount(soldBatch.getAmount());
            soldBatchResponseDTO.setDteNumber(soldBatch.getDteNumber());
            soldBatchResponseDTO.setCategory(soldBatch.getAnimalsOnGround().getCategory());
            soldBatchResponseDTO.setSeller(clientService.findByProvenanceId(batchService.getBatchByAnimalsOnGroundId(soldBatch.getAnimalsOnGround().getId()).getProvenance().getId()));
            responseDTOList.add(soldBatchResponseDTO);
        }
        return new PageImpl<>(responseDTOList, p, soldBatches.getTotalElements());
    }
}

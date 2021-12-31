package pfc.consignacionhacienda.services.notSoldBatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.NotSoldBatchDAO;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.model.NotSoldBatch;
import pfc.consignacionhacienda.model.SoldBatch;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.client.ClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Page<NotSoldBatch> soldBatches = notSoldBatchDAO.findByAuctionId(auctionId, p);
        List<SoldBatchResponseDTO> responseDTOList = new ArrayList<>();
        for(NotSoldBatch notSoldBatch: soldBatches){
            SoldBatchResponseDTO soldBatchResponseDTO = new SoldBatchResponseDTO();
            soldBatchResponseDTO.setAmount(notSoldBatch.getAmount());
            soldBatchResponseDTO.setDteNumber(notSoldBatch.getDteNumber());
            soldBatchResponseDTO.setCategory(notSoldBatch.getAnimalsOnGround().getCategory());
            soldBatchResponseDTO.setSeller(clientService.findByProvenanceId(batchService.getBatchByAnimalsOnGroundId(notSoldBatch.getAnimalsOnGround().getId()).getProvenance().getId()));
            responseDTOList.add(soldBatchResponseDTO);
        }
        return new PageImpl<>(responseDTOList, p, soldBatches.getTotalElements());
    }

    @Override
    public NotSoldBatch save(NotSoldBatch notSoldBatch) {
        return notSoldBatchDAO.save(notSoldBatch);
    }

    @Override
    public List<NotSoldBatch> saveAll(List<NotSoldBatch> notSoldBatches) {
        return notSoldBatchDAO.saveAll(notSoldBatches);
    }

    @Override
    public Optional<NotSoldBatch> getNotSoldBatchesByAnimalsOnGroundId(Integer id) {
        return notSoldBatchDAO.getByAnimalsOnGroundId(id);
    }

    @Override
    public List<NotSoldBatch> deleteAll(List<NotSoldBatch> notSoldBatchesToDelete) {
        notSoldBatchDAO.deleteAll(notSoldBatchesToDelete);
        return notSoldBatchesToDelete;
    }
}

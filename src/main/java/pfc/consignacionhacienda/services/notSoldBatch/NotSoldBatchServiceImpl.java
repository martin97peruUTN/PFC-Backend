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
    public List<NotSoldBatch> deleteAllByAuctionId(Integer auctionId) {
        //Esto capaz se podria mejorar con una consulta especifica para hacer eliminaciones multiples
        //pero es algo complicado y no creo que valga la pena, esta funcionalidad no deberia ser muy usada
        List<NotSoldBatch> list = notSoldBatchDAO.findAllByAuctionId(auctionId);
        notSoldBatchDAO.deleteAll(list);
        return list;
    }
}

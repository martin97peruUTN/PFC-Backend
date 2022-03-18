package pfc.consignacionhacienda.services.notSoldBatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.NotSoldBatchDAO;
import pfc.consignacionhacienda.dto.NotSoldBatchDTO;
import pfc.consignacionhacienda.dto.SoldBatchDTO;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.notSoldBatch.NotSoldBatchNotFoundException;
import pfc.consignacionhacienda.exceptions.soldBatch.SoldBatchNotFoundException;
import pfc.consignacionhacienda.model.*;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.NotSoldBatchMapper;

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

    @Autowired
    private NotSoldBatchMapper notSoldBatchMapper;

    @Override
    public Page<SoldBatchResponseDTO> getNotSoldBatchesByAuctionAndPage(Integer auctionId, Integer page, Integer limit) {
        Pageable p = PageRequest.of(page, limit);
        Page<NotSoldBatch> soldBatches = notSoldBatchDAO.findByAuctionId(auctionId, p);
        List<SoldBatchResponseDTO> responseDTOList = new ArrayList<>();
        for(NotSoldBatch notSoldBatch: soldBatches){
            SoldBatchResponseDTO soldBatchResponseDTO = new SoldBatchResponseDTO();
            soldBatchResponseDTO.setId(notSoldBatch.getId());
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

    @Override
    public NotSoldBatch updateNotSoldBatchById(NotSoldBatchDTO notSoldBatchDTO, Integer notSoldBatchId) throws IllegalArgumentException, NotSoldBatchNotFoundException {
        if(notSoldBatchDTO.getId() != null && !notSoldBatchId.equals(notSoldBatchDTO.getId())){
            throw new IllegalArgumentException("Los id del path y del objeto a editar son distintos");
        }
        NotSoldBatch notSoldBatch = findByIdNotDeleted(notSoldBatchId);
        notSoldBatchMapper.updateNotSoldBatchFromDto(notSoldBatchDTO, notSoldBatch);
        return notSoldBatchDAO.save(notSoldBatch);
    }

    private NotSoldBatch findByIdNotDeleted(Integer notSoldBatchId) throws NotSoldBatchNotFoundException {
        Optional<NotSoldBatch> notSoldBatchOpt = notSoldBatchDAO.findById(notSoldBatchId);
        if(notSoldBatchOpt.isPresent()){
            return notSoldBatchOpt.get();
        }
        throw new NotSoldBatchNotFoundException("El lote no vendido con id: " + notSoldBatchId + " no existe.");
    }
}

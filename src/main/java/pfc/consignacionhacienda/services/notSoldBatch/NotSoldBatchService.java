package pfc.consignacionhacienda.services.notSoldBatch;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.dto.NotSoldBatchDTO;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.exceptions.notSoldBatch.NotSoldBatchNotFoundException;
import pfc.consignacionhacienda.model.NotSoldBatch;

import java.util.List;
import java.util.Optional;

public interface NotSoldBatchService {
    Page<SoldBatchResponseDTO> getNotSoldBatchesByAuctionAndPage(Integer auctionId, Integer page, Integer limit);

    NotSoldBatch save(NotSoldBatch notSoldBatch);

    List<NotSoldBatch> saveAll(List<NotSoldBatch> notSoldBatches);

    Optional<NotSoldBatch> getNotSoldBatchesByAnimalsOnGroundId(Integer id);

    List<NotSoldBatch> deleteAll(List<NotSoldBatch> notSoldBatchesToDelete);

    NotSoldBatch updateNotSoldBatchById(NotSoldBatchDTO notSoldBatchDTO, Integer notSoldBatchId) throws NotSoldBatchNotFoundException;
}

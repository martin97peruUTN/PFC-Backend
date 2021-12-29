package pfc.consignacionhacienda.services.notSoldBatch;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.model.NotSoldBatch;

import java.util.List;

public interface NotSoldBatchService {
    Page<SoldBatchResponseDTO> getNotSoldBatchesByAuctionAndPage(Integer auctionId, Integer page, Integer limit);

    NotSoldBatch save(NotSoldBatch notSoldBatch);

    List<NotSoldBatch> saveAll(List<NotSoldBatch> notSoldBatches);

    List<NotSoldBatch> deleteAllByAuctionId(Integer auctionId);
}

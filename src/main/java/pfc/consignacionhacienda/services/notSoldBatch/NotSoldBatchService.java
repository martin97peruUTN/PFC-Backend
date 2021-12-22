package pfc.consignacionhacienda.services.notSoldBatch;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;

public interface NotSoldBatchService {
    Page<SoldBatchResponseDTO> getNotSoldBatchesByAuctionAndPage(Integer auctionId, Integer page, Integer limit);
}

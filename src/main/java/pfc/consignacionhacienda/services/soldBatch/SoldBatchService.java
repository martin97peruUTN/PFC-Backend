package pfc.consignacionhacienda.services.soldBatch;

import pfc.consignacionhacienda.model.SoldBatch;

import java.util.List;

public interface SoldBatchService {
    Integer getTotalSold(Integer id);

    List<SoldBatch> findSoldBatchesNotDeletedByAnimalsOnGroundId(Integer id);
}

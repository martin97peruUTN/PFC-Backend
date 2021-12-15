package pfc.consignacionhacienda.services.soldBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.SoldBatchDAO;
import pfc.consignacionhacienda.model.SoldBatch;

import java.util.List;

@Service
public class SoldBatchServiceImpl implements SoldBatchService{

    private static final Logger logger = LoggerFactory.getLogger(SoldBatchServiceImpl.class);

    @Autowired
    SoldBatchDAO soldBatchDAO;

    @Override
    public Integer getTotalSold(Integer id) {
        Integer total = soldBatchDAO.getTotalSold(id);
        return total==null?0:total;
    }

    @Override
    public List<SoldBatch> findSoldBatchesNotDeletedByAnimalsOnGroundId(Integer id) {
        return soldBatchDAO.findSoldBatchesNotDeletedByAnimalsOnGroundId(id);
    }
}

package pfc.consignacionhacienda.services.soldBatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.SoldBatchDAO;

@Service
public class SoldBatchServiceImpl implements SoldBatchService{
    @Autowired
    SoldBatchDAO soldBatchDAO;

    @Override
    public Integer getTotalSold(Integer id) {
        return soldBatchDAO.getTotalSold(id);
    }
}

package pfc.consignacionhacienda.services.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import pfc.consignacionhacienda.model.SoldBatch;
import pfc.consignacionhacienda.services.batch.BatchService;

import javax.persistence.*;

@Component
public class DbListener {

    private static final Logger logger = LoggerFactory.getLogger(DbListener.class);

//    @Autowired
//    private SimpMessagingTemplate websocket;

    @Autowired
//    BatchService batchService;
    public void init(SimpMessagingTemplate websocket)
    {
        if(DbListener.websocket == null) {
            DbListener.websocket = websocket;
        }
    }

    static private SimpMessagingTemplate websocket;

    @Autowired
//    BatchService batchService;
    public void init(BatchService batchService)
    {
        if(DbListener.batchService == null) {
            DbListener.batchService = batchService;
        }
    }

    static private BatchService batchService;


    @PostPersist
    @PostUpdate
    @PostRemove
    private void postAnyUpdate(SoldBatch soldBatch) {
        Integer id = batchService.getBatchByAnimalsOnGroundId(soldBatch.getAnimalsOnGround().getId()).getAuction().getId();
        websocket.convertAndSend(
                WebSocketConfiguration.MESSAGE_PREFIX + "/sold-batch/"+id, soldBatch.toString());
    }

}

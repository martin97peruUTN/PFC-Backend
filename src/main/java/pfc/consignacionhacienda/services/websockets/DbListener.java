package pfc.consignacionhacienda.services.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pfc.consignacionhacienda.model.SoldBatch;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class DbListener {

    private static final Logger logger = LoggerFactory.getLogger(DbListener.class);

    @Autowired
    private SimpMessagingTemplate websocket;

    @PostPersist
    @PostUpdate
    @PostRemove
    private void postAnyUpdate(SoldBatch soldBatch) {
        logger.debug("add/update/delete complete for user: " + soldBatch.toString());
        this.websocket.convertAndSend(
                WebSocketConfiguration.MESSAGE_PREFIX + "/newSoldBatch", soldBatch.toString());
    }

}

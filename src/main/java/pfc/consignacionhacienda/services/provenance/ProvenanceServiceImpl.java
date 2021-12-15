package pfc.consignacionhacienda.services.provenance;

import org.springframework.beans.factory.annotation.Autowired;
import pfc.consignacionhacienda.dao.ProvenanceDAO;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;

public class ProvenanceServiceImpl implements ProvenanceService{
    @Autowired
    ProvenanceDAO provenanceDAO;
    @Override
    public Client findClientByProvenanceId(Integer provenanceId) throws ClientNotFoundException {
        Client c = provenanceDAO.findClientByProvenanceId(provenanceId);
        if(c.getDeleted() != null && c.getDeleted()){
            throw new ClientNotFoundException("La procedencia con id: " + provenanceId +" es de un cliente inexistente.");
        }
        return c;
    }
}

package pfc.consignacionhacienda.services.provenance;

import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;

public interface ProvenanceService {
    Client findClientByProvenanceId(Integer provenanceId) throws ClientNotFoundException;
}

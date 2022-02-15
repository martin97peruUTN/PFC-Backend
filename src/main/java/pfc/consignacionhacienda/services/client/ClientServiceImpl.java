package pfc.consignacionhacienda.services.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.ClientDAO;
import pfc.consignacionhacienda.dao.ProvenanceDAO;
import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.dto.ProvenanceDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;
import pfc.consignacionhacienda.model.Provenance;
import pfc.consignacionhacienda.utils.ClientMapper;
import pfc.consignacionhacienda.utils.ProvenanceMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{
    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    @Autowired
    private ClientDAO clientDAO;
    @Autowired
    private ProvenanceDAO provenanceDAO;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ProvenanceMapper provenanceMapper;

    @Override
    public Client getClientById(Integer id) throws ClientNotFoundException {
        Optional<Client> clientOptional = clientDAO.findById(id);
        if(clientOptional.isPresent()){
            Client c = clientOptional.get();
            c.setProvenances(provenanceDAO.getProvenancesNotDeletedByClientId(c.getId()));
            return c;
        }
        throw new ClientNotFoundException("El cliente con id: " + id + " no existe." );
    }

    @Override
    public Page<Client> getClientsByPage(Integer page, Integer size, String name) {
        Page<Client> clients = clientDAO.findByNotDeletedAndName(name, PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));
        return clients.map((c) -> {
            c.setProvenances(provenanceDAO.getProvenancesNotDeletedByClientId(c.getId()));
            return c;
        }); //clientDAO.findByNotDeletedAndName(name, PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));
    }

    @Override
    public Client deleteClientById(Integer id) throws ClientNotFoundException {
        Client c = getClientById(id);
        if(c.getDeleted() != null && c.getDeleted()){
            throw new ClientNotFoundException("El cliente con id: " + id + " no existe.");
        }
        c.setDeleted(true);
        return clientDAO.save(c);
    }

    @Override
    public Client updateClientById(ClientDTO clientDTO, Integer id) throws ClientNotFoundException, BadHttpRequest {
        List<ProvenanceDTO> auxiliar = clientDTO.getDeletedProvenances();
        boolean error = false;
        if (clientDTO.getDeletedProvenances() != null) {
            logger.debug(clientDTO.getDeletedProvenances().toString());
           List<ProvenanceDTO> pList = clientDTO.getDeletedProvenances();
           List<Provenance> provenancesToSave = new ArrayList<>();
           int counter = 0;
           while(counter < pList.size() && !error){
                ProvenanceDTO p = pList.get(counter);
                if(provenanceDAO.isBeingUsed(p.getId())){
                    error = true;
                }
                counter++;
                if(!error) {
                    Provenance provenance = provenanceDAO.findById(p.getId()).get();
                    ProvenanceDTO aux = new ProvenanceDTO();
                    aux.setDeleted(true);
                    provenanceMapper.updateProvenanceFromDto(aux, provenance);
                    provenancesToSave.add(provenance);
                }
//                logger.debug(provenance.toString());
//                provenanceDAO.save(provenance);
            }
           if(!error){
               provenanceDAO.saveAllAndFlush(provenancesToSave);
           }
            clientDTO.setDeletedProvenances(null);
        }
        Client c = getClientById(id);
        clientMapper.updateClientFromDto(clientDTO, c);
        try {
            if(error){
                throw new BadHttpRequest("No se pueden eliminar procedencias que están siendo utilizadas por remates en progreso");
            }
            return saveClient(c);
        } catch (BadHttpRequest e){
            //Esto lo hago porque si al momento de guardar el cliente modificado lanza un error porque no quedar procedencias, debo mantener el estado inicial con las procedencias originales sin ser eliminadas.
            if(auxiliar != null && !error) {
                for (ProvenanceDTO p : auxiliar) {
                    Provenance provenance = provenanceDAO.findById(p.getId()).get();
                    ProvenanceDTO aux = new ProvenanceDTO();
                    aux.setDeleted(false);
                    provenanceMapper.updateProvenanceFromDto(aux, provenance);
                    logger.debug(provenance.toString());
                    provenanceDAO.save(provenance);
                }
            }
            throw e;
        }
    }

    @Override
    public Client saveClient(Client client) throws BadHttpRequest {
        if(client.getProvenances() != null && !client.getProvenances().isEmpty()) {
            return clientDAO.save(client);
        }
        throw new BadHttpRequest("El cliente debe tener al menos una procedencia.");
    }

    @Override
    public Client findByProvenanceId(Integer id) {
        return clientDAO.findByProvenanceContaining(id);
    }
}

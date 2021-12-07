package pfc.consignacionhacienda.services.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.ClientDAO;
import pfc.consignacionhacienda.dao.ProvenanceDAO;
import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;
import pfc.consignacionhacienda.model.Provenance;
import pfc.consignacionhacienda.utils.ClientMapper;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{
    @Autowired
    private ClientDAO clientDAO;
    @Autowired
    private ProvenanceDAO provenanceDAO;
    @Autowired
    private ClientMapper clientMapper;

    @Override
    public Client getClientById(Integer id) throws ClientNotFoundException {
        Optional<Client> clientOptional = clientDAO.findById(id);
        if(clientOptional.isPresent()){
            return clientOptional.get();
        }
        throw new ClientNotFoundException("El cliente con id: " + id + " no existe." );
    }

    @Override
    public Page<Client> getClientsByPage(Integer page, Integer size, String name) {
        return clientDAO.findByNotDeletedAndName(name, PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));
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
    public Client updateClientById(ClientDTO clientDTO, Integer id) throws ClientNotFoundException {
        if(clientDTO.getDeletedProvenances() != null) {
            for (Provenance p : clientDTO.getDeletedProvenances()) {
                p.setDeleted(true);
                provenanceDAO.save(p);
            }
            clientDTO.setDeletedProvenances(null);
        }

        Client c = getClientById(id);
        clientMapper.updateClientFromDto(clientDTO, c);
        return saveClient(c);
    }

    @Override
    public Client saveClient(Client client) {
        return clientDAO.save(client);
    }
}

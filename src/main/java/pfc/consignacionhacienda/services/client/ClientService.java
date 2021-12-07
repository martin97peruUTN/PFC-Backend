package pfc.consignacionhacienda.services.client;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;

public interface ClientService {
    Client getClientById(Integer id) throws ClientNotFoundException;
    Page<Client> getClientsByPage(Integer page, Integer size, String name);
    Client deleteClientById(Integer id) throws ClientNotFoundException;
    Client updateClientById(ClientDTO clientDTO, Integer id) throws ClientNotFoundException, BadHttpRequest;
    Client saveClient(Client client);
}

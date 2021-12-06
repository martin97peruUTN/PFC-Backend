package pfc.consignacionhacienda.services.client;

import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;

import java.util.List;

public interface ClientService {
    Client getClientById(Integer id) throws ClientNotFoundException;
    List<Client> getClientsByPage(Integer size, Integer page, String name);
    Client deleteClientById(Integer id) throws ClientNotFoundException;
    Client updateClientById(ClientDTO clientDTO, Integer id) throws ClientNotFoundException;
    Client saveClient(Client client);
}

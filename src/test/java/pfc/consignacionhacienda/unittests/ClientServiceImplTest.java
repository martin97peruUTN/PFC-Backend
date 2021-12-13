package pfc.consignacionhacienda.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import pfc.consignacionhacienda.dao.ClientDAO;
import pfc.consignacionhacienda.dao.ProvenanceDAO;
import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.dto.ProvenanceDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;
import pfc.consignacionhacienda.model.Locality;
import pfc.consignacionhacienda.model.Provenance;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.utils.ClientMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ClientServiceImplTest {
    @SpyBean
    private ClientService clientService;

    private Client client;
    private ArrayList<Provenance> provenances;

    @Autowired
    private ClientMapper clientMapper;

    @MockBean
    private ClientDAO clientDAO;

    @MockBean
    private ProvenanceDAO provenanceDAO;

    @BeforeEach
    void initTests(){
        client = new Client();
        client.setCuit("2040905305");
        client.setName("client test");
        provenances = new ArrayList<>();
        Provenance p1 = new Provenance();
        p1.setReference("reference");
        p1.setRenspaNumber("renspa");
        Locality locality = new Locality();
        locality.setId(1);
        locality.setName("La Criolla");
        locality.setDeleted(false);
        p1.setLocality(locality);
        Provenance p2 = new Provenance();
        p2.setReference("reference");
        p2.setRenspaNumber("renspa");
        p2.setLocality(locality);
        provenances.add(p1);
        provenances.add(p2);
        client.setProvenances(provenances);
    }

    @Test
    void createClientSuccesfully(){
        client.setId(1);
//        Mockito.doReturn(client).when(clientDAO).save(any(Client.class));
        when(clientDAO.save(any(Client.class))).thenReturn(client);
        try {
            assertNotNull(clientService.saveClient(client).getId());
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
    }

    @Test
    void createClientWithoutProvenance(){
        client.setId(1);
        when(clientDAO.save(any(Client.class))).thenReturn(client);
        client.setProvenances(null);
        assertThrows(BadHttpRequest.class, ()->clientService.saveClient(client));
    }

    @Test
    void createClientWithoutName(){
        client.setId(1);
        client.setName(null);
        when(clientDAO.save(any(Client.class))).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> clientService.saveClient(client));
    }

    @Test
    void deleteExistentUser(){
        client.setId(1);
        when(clientDAO.save(any(Client.class))).thenReturn(client);
        when(clientDAO.findById(any(Integer.class))).thenReturn(Optional.of(client));
        assertNull(client.getDeleted());
        try {
            client = clientService.deleteClientById(1);
        } catch (ClientNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(1, client.getId());
        assertTrue(client.getDeleted());
    }
    @Test
    void deleteDeletedUser(){
        client.setId(1);
        client.setDeleted(true);
        when(clientDAO.save(any(Client.class))).thenReturn(client);
        when(clientDAO.findById(any(Integer.class))).thenReturn(Optional.of(client));
        assertThrows(ClientNotFoundException.class, () -> clientService.deleteClientById(1));
    }
    @Test
    void deleteInexistentUser(){
        client.setId(1);
        when(clientDAO.save(any(Client.class))).thenReturn(client);
        when(clientDAO.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(ClientNotFoundException.class, () -> clientService.deleteClientById(2));
    }

    @Test
    void updateUser(){
        client.setId(1);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("nuevoNombre");
        client.setName("nuevoNombre");
        when(clientDAO.findById(any(Integer.class))).thenReturn(Optional.of(client));
        try {
            when(clientDAO.save(any(Client.class))).thenReturn(client);
            client = clientService.updateClientById(clientDTO,1);
        } catch (ClientNotFoundException| BadHttpRequest e) {
            e.printStackTrace();
        }
        assertEquals(client.getName(),"nuevoNombre");
    }

    @Test
    void updateUserDeleteAllProvenances(){
        client.setId(1);
        ClientDTO clientDTO = new ClientDTO();
        ProvenanceDTO p1 = new ProvenanceDTO();
        p1.setId(1);
        ProvenanceDTO p2 = new ProvenanceDTO();
        p2.setId(2);
        clientDTO.setDeletedProvenances(List.of(p1, p2));
        when(clientDAO.findById(any(Integer.class))).thenReturn(Optional.of(client));
        Provenance provenance = new Provenance();
        provenance.setId(1);
        when(provenanceDAO.findById(any(Integer.class))).thenReturn(Optional.of(provenance));
        when(provenanceDAO.save(any(Provenance.class))).thenReturn(provenance);
        try {
            Mockito.doThrow(BadHttpRequest.class).when(clientService).saveClient(any(Client.class));
        } catch (BadHttpRequest e) {
            assertThrows(BadHttpRequest.class, () -> clientService.updateClientById(clientDTO,1));
            verify(provenanceDAO, times(4)).save(any(Provenance.class));
        }
    }

    @Test
    void updateUserDeleteSomeProvenances(){
        client.setId(1);
        ClientDTO clientDTO = new ClientDTO();
        ProvenanceDTO p1 = new ProvenanceDTO();
        p1.setId(1);
        ProvenanceDTO p2 = new ProvenanceDTO();
        p2.setId(2);
        clientDTO.setDeletedProvenances(List.of(p1));
        when(clientDAO.findById(any(Integer.class))).thenReturn(Optional.of(client));
        Provenance provenance = new Provenance();
        provenance.setId(1);
        when(provenanceDAO.findById(any(Integer.class))).thenReturn(Optional.of(provenance));
        when(provenanceDAO.save(any(Provenance.class))).thenReturn(provenance);
        when(clientDAO.save(any(Client.class))).thenReturn(client);
        try {
            clientService.updateClientById(clientDTO, 1);
        } catch (ClientNotFoundException | BadHttpRequest e) {
            e.printStackTrace();
        }
        verify(provenanceDAO, times(1)).save(any(Provenance.class));
    }

    @Test
    void updateUserDeleteAndAddSomeProvenances(){
        client.setId(1);
        ClientDTO clientDTO = new ClientDTO();
        ProvenanceDTO p1 = new ProvenanceDTO();
        p1.setId(1);
        ProvenanceDTO p2 = new ProvenanceDTO();
        p2.setId(2);
        Provenance p3 = new Provenance();
        p3.setId(3);
        Provenance p4 = new Provenance();
        p4.setId(4);
        clientDTO.setDeletedProvenances(List.of(p1,p2));
        clientDTO.setProvenances(List.of(p3,p4));
        when(clientDAO.findById(any(Integer.class))).thenReturn(Optional.of(client));
        Provenance provenance = new Provenance();
        provenance.setId(1);
        when(provenanceDAO.findById(any(Integer.class))).thenReturn(Optional.of(provenance));
        when(provenanceDAO.save(any(Provenance.class))).thenReturn(provenance);
        when(clientDAO.save(any(Client.class))).thenReturn(client);
        try {
            clientService.updateClientById(clientDTO, 1);
        } catch (ClientNotFoundException | BadHttpRequest e) {
            e.printStackTrace();
        }
        verify(provenanceDAO, times(2)).save(any(Provenance.class));
        clientDTO.setDeletedProvenances(null);
        clientMapper.updateClientFromDto(clientDTO, client);
        assertEquals(client.getProvenances().size(),2);
    }
}

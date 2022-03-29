package pfc.consignacionhacienda.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.RestTemplate;
import pfc.consignacionhacienda.dao.ProvenanceDAO;
import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.dto.ProvenanceDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.exceptions.user.DuplicateUsernameException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.Client;
import pfc.consignacionhacienda.model.Locality;
import pfc.consignacionhacienda.model.Provenance;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.ClientPageDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientRestTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientRestTest.class);
    private TestRestTemplate testRestTemplate = new TestRestTemplate();
    private RestTemplate testRestTemplatePatch = testRestTemplate.getRestTemplate();

    @LocalServerPort
    String puerto;

    @Mock
    Collection<? extends GrantedAuthority> list2;

    private List<GrantedAuthority> roles;

    @SpyBean
    private UserService userService;

    private Client client;
    private ArrayList<Provenance> provenances;

    private ObjectMapper objectMapper;

    @Autowired
    ClientService clientService;

    @Autowired
    ProvenanceDAO provenanceDAO;

    @BeforeEach
    void initTests() throws DuplicateUsernameException, BadHttpRequest {
        objectMapper = new ObjectMapper();
        roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("Administrador"));
        when(list2.toArray()).thenReturn(roles.toArray());
        Mockito.doReturn(list2).when(userService).getCurrentUserAuthorities();
        User u;
        try{
            u = userService.findUserById(1);
        } catch (UserNotFoundException e){
            u = new User();
            u.setPassword("1234");
            u.setRol("Administrador");
            u.setName("testUser");
            u.setLastname("lastname");
            u.setUsername(UUID.randomUUID().toString());
            u = userService.saveUser(u);
        }
        ArrayList<User> users = new ArrayList<>();
        users.add(u);
        Mockito.doReturn(u).when(userService).getCurrentUser();

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
        String server = "http://localhost:" + puerto + "/api/client";
        try {
            assertNull(client.getId());
            String clientJSON = objectMapper.writeValueAsString(client);
            logger.debug(clientJSON);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> newClient = new HttpEntity<>(clientJSON, headers);
            ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.POST, newClient,
                    String.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            client = objectMapper.readValue(response.getBody(), Client.class);
            assertNotNull(client.getId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createClientWithoutProvenance(){
        String server = "http://localhost:" + puerto + "/api/client";
        client.setProvenances(null);
        try {
            assertNull(client.getId());
            String clientJSON = objectMapper.writeValueAsString(client);
            logger.debug(clientJSON);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> newClient = new HttpEntity<>(clientJSON, headers);
            ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.POST, newClient,
                    String.class);
            assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createClientWithoutName(){
        String server = "http://localhost:" + puerto + "/api/client";
        client.setName(null);
        try {
            assertNull(client.getId());
            String clientJSON = objectMapper.writeValueAsString(client);
            logger.debug(clientJSON);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> newClient = new HttpEntity<>(clientJSON, headers);
            ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.POST, newClient,
                    String.class);
            assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteClientSuccesfully(){
        try {
            client = clientService.saveClient(client);
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        String server = "http://localhost:" + puerto + "/api/client/"+client.getId();
        try {
            ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.DELETE, HttpEntity.EMPTY ,
                    String.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            client = objectMapper.readValue(response.getBody(), Client.class);
            assertTrue(client.getDeleted());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteDeletedClient(){
        try {
            client = clientService.saveClient(client);
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        try {
            client = clientService.deleteClientById(client.getId());
            assertTrue(client.getDeleted());
        } catch (ClientNotFoundException e) {
            e.printStackTrace();
        }
        String server = "http://localhost:" + puerto + "/api/client/"+client.getId();
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.DELETE, HttpEntity.EMPTY ,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteInexistentClient(){
        String server = "http://localhost:" + puerto + "/api/client/0";
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.DELETE, HttpEntity.EMPTY ,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void updateClientNotProvenance(){
        try {
            client = clientService.saveClient(client);
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Name editado");
        String server = "http://localhost:" + puerto + "/api/client/"+client.getId();
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<String> auctionDTOHttpEntity = null;
        try {
            assertNotEquals(client.getName(), clientDTO.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            auctionDTOHttpEntity = new HttpEntity<>(objectMapper.writeValueAsString(clientDTO), headers);
            ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
                    String.class);
            logger.debug(response.getBody());
            client = objectMapper.readValue(response.getBody(), Client.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(client.getName(), clientDTO.getName());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateClientDeleteAllProvenances(){
        try {
            client = clientService.saveClient(client);
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        List<Provenance> provenances = client.getProvenances();
        ArrayList<ProvenanceDTO> deletedProvenances = new ArrayList<>();
        for(Provenance p: provenances){
            ProvenanceDTO p1 = new ProvenanceDTO();
            p1.setId(p.getId());
            deletedProvenances.add(p1);
        }
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setDeletedProvenances(deletedProvenances);
        String server = "http://localhost:" + puerto + "/api/client/"+client.getId();
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<String> auctionDTOHttpEntity = null;
        try {
            assertNotEquals(client.getName(), clientDTO.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            auctionDTOHttpEntity = new HttpEntity<>(objectMapper.writeValueAsString(clientDTO), headers);
            ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
                    String.class);
            logger.debug(response.getBody());
//            client = objectMapper.readValue(response.getBody(), Client.class);
            assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
            client = clientService.getClientById(client.getId());
            List<Provenance> provenancesSaved = client.getProvenances();
            for(Provenance p: provenancesSaved){
                assertTrue(p.getDeleted() == null || !p.getDeleted());
            }
        } catch (JsonProcessingException | ClientNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    void updateClientDeleteSomeProvenances(){
        try {
            client = clientService.saveClient(client);
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        List<Provenance> provenances = client.getProvenances();
        int cantProvenances = provenances.size();
        ProvenanceDTO p1 = new ProvenanceDTO();
        p1.setId(provenances.get(0).getId());
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setDeletedProvenances(List.of(p1));
        String server = "http://localhost:" + puerto + "/api/client/"+client.getId();
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<String> auctionDTOHttpEntity = null;
        try {
            assertNotEquals(client.getName(), clientDTO.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            auctionDTOHttpEntity = new HttpEntity<>(objectMapper.writeValueAsString(clientDTO), headers);
            ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
                    String.class);
            logger.debug(response.getBody());
            client = objectMapper.readValue(response.getBody(), Client.class);
            Provenance provenanceDeleted = provenanceDAO.findById(clientDTO.getDeletedProvenances().get(0).getId()).get();
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(cantProvenances-clientDTO.getDeletedProvenances().size(), client.getProvenances().size() );
            assertTrue(provenanceDeleted.getDeleted());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateClientDeleteAllProvenancesAndAddOthers(){
        try {
            client = clientService.saveClient(client);
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        List<Provenance> provenances = client.getProvenances();
        ArrayList<ProvenanceDTO> deletedProvenances = new ArrayList<>();
        for(Provenance p: provenances){
            ProvenanceDTO p1 = new ProvenanceDTO();
            p1.setId(p.getId());
            deletedProvenances.add(p1);
        }

        provenances = new ArrayList<>();
        Provenance p1 = new Provenance();
        p1.setReference("reference");
        p1.setRenspaNumber("renspa");
        Locality locality = new Locality();
        locality.setId(2);
        p1.setLocality(locality);
        p1.setReference("referenceNew");
        p1.setRenspaNumber("renspaNew");
        Provenance p2 = new Provenance();
        p2.setLocality(locality);
        p2.setReference("referenceNew");
        p2.setRenspaNumber("renspaNew");
        provenances.add(p1);
        provenances.add(p2);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setDeletedProvenances(deletedProvenances);
        clientDTO.setProvenances(provenances);
        String server = "http://localhost:" + puerto + "/api/client/"+client.getId();
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<String> auctionDTOHttpEntity = null;
        try {
            assertNotEquals(client.getName(), clientDTO.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            auctionDTOHttpEntity = new HttpEntity<>(objectMapper.writeValueAsString(clientDTO), headers);
            ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
                    String.class);
            logger.debug(response.getBody());
            client = objectMapper.readValue(response.getBody(), Client.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(client.getProvenances().size(), 2);
            for(Provenance p: client.getProvenances()){
                assertEquals(p.getReference(), p1.getReference());
                assertEquals(p.getRenspaNumber(), p1.getRenspaNumber());
            }
            for(ProvenanceDTO p: deletedProvenances){
                Provenance prov= provenanceDAO.findById(p.getId()).get();
                assertTrue(prov.getDeleted());
            }
            Client clientDB = clientService.getClientById(client.getId());
            assertEquals(clientDB.getProvenances().size(), 2);
        } catch (JsonProcessingException | ClientNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTwoClientsFirstPage() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        String server = "http://localhost:" + puerto + "/api/client";
        String clientJSON = objectMapper.writeValueAsString(client);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> newClient = new HttpEntity<>(clientJSON, headers);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.POST, newClient,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        newClient = new HttpEntity<>(clientJSON, headers);
        response = testRestTemplate.exchange(server, HttpMethod.POST, newClient,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        server = "http://localhost:" + puerto + "/api/client?page=0&limit=2";
        response = testRestTemplate.getForEntity(server, String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        try {
            ClientPageDTO clientPageDTO = objectMapper.readValue(response.getBody(), ClientPageDTO.class);
            assertEquals(clientPageDTO.getContent().size(),2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getClientByID(){
        try {
            client = clientService.saveClient(client);
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        String server = "http://localhost:" + puerto + "/api/client/"+client.getId();
        ResponseEntity<String> response = testRestTemplate.getForEntity(server, String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        try {
            client = objectMapper.readValue(response.getBody(), Client.class);
            assertNotNull(client.getId());
            assertTrue(client.getDeleted() == null || !client.getDeleted());
            client = clientService.deleteClientById(client.getId());
            response = testRestTemplate.getForEntity(server, String.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertTrue(client.getDeleted());
        } catch (JsonProcessingException | ClientNotFoundException e) {
            e.printStackTrace();
        }
    }
}

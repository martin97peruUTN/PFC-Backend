package pfc.consignacionhacienda.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.RestTemplate;
import pfc.consignacionhacienda.dto.AuctionDTO;
import pfc.consignacionhacienda.exceptions.locality.LocalityNotFoundException;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.locality.LocalityService;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.unittests.AuctionServiceImplTest;
import pfc.consignacionhacienda.utils.AuctionPageDTO;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuctionRestTest {
    private static final Logger logger = LoggerFactory.getLogger(AuctionServiceImplTest.class);
    private TestRestTemplate testRestTemplate = new TestRestTemplate();
    private RestTemplate testRestTemplatePatch = testRestTemplate.getRestTemplate();

    @LocalServerPort
    String puerto;

    @SpyBean
    private UserService userService;

    @SpyBean
    private LocalityService localityService;

    private Auction auction;

    @Mock
    Collection<? extends GrantedAuthority> list2;

    private List<GrantedAuthority> roles;

    @BeforeEach
    void initTests(){
        roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("Administrador"));
        when(list2.toArray()).thenReturn(roles.toArray());
        Mockito.doReturn(list2).when(userService).getCurrentUserAuthorities();

        User u = userService.findUserById(1);
        ArrayList<User> users = new ArrayList<>();
        users.add(u);
        Mockito.doReturn(u).when(userService).getCurrentUser();

        auction = new Auction();
        auction.setDeleted(false);
        auction.setFinished(false);
        try {
            auction.setLocality(localityService.getLocalityById(1));
        } catch (LocalityNotFoundException e) {
            e.printStackTrace();
        }
        auction.setSenasaNumber("aNumber");
        auction.setDate(Instant.now().plus(Period.ofDays(10)));
        auction.setUsers(users);
    }

    //Crear remates
    @Test
    void createAuctionSuccesfully(){
        String server = "http://localhost:" + puerto + "/api/auction";
        HttpEntity<Auction> newAuction = new HttpEntity<>(auction);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.POST, newAuction,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void createAuctionWithoutLocality(){
        String server = "http://localhost:" + puerto + "/api/auction";
        auction.setLocality(null);
        HttpEntity<Auction> newAuction = new HttpEntity<>(auction);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.POST, newAuction,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void createAuctionWithoutUser(){
        String server = "http://localhost:" + puerto + "/api/auction";
        auction.setUsers(null);
        HttpEntity<Auction> newAuction = new HttpEntity<>(auction);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.POST, newAuction,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    @Test
    void createAuctionWithInvalidDate(){
        auction.setDate(Instant.now().minus(Period.ofDays(10)));
        String server = "http://localhost:" + puerto + "/api/auction";
        HttpEntity<Auction> newAuction = new HttpEntity<>(auction);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.POST, newAuction,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    //-----------------
    //Actualizar remates
    @Test
    void updateAuctionSuccesfully(){
        AuctionDTO auctionDTO = new AuctionDTO();
        auction.setId(1);
        try {
            assertEquals(auction.getLocality().getId(),1);
            auctionDTO.setLocality(localityService.getLocalityById(2));
            logger.debug(auction.toString());
            logger.debug(auction.toString());
        } catch (LocalityNotFoundException e) {
            e.printStackTrace();
        }
        String server = "http://localhost:" + puerto + "/api/auction/"+auction.getId();

        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<AuctionDTO> auctionDTOHttpEntity = new HttpEntity<>(auctionDTO);
        ResponseEntity<Auction> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
                Auction.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getId(),1);
        assertEquals(response.getBody().getLocality().getId(),2);
    }
//
    @Test
    void updateAuctionWithInvalidDate(){
        auction.setId(1);
        String server = "http://localhost:" + puerto + "/api/auction/"+auction.getId();
        AuctionDTO auctionDTO = new AuctionDTO();
        auctionDTO.setDate(Instant.now().minus(Period.ofDays(10)));
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<AuctionDTO> auctionDTOHttpEntity = new HttpEntity<>(auctionDTO);
        ResponseEntity<Auction> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
            Auction.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateAuctionWithInvalidParticipants(){
        AuctionDTO auctionDTO = new AuctionDTO();
        ArrayList<User> users = new ArrayList<>();
        User u = new User();
        u.setId(1);
        u.setRol("Asistente");
        users.add(u);
        auctionDTO.setUsers(users);
        auction.setId(1);
        String server = "http://localhost:" + puerto + "/api/auction/"+auction.getId();
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<AuctionDTO> auctionDTOHttpEntity = new HttpEntity<>(auctionDTO);
        ResponseEntity<Auction> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
                Auction.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateAuctionWithValidDate(){
        AuctionDTO auctionDTO = new AuctionDTO();
        assertEquals(auction.getLocality().getId(),1);
        Instant before = auction.getDate();
        auctionDTO.setDate(auction.getDate().plus(Period.ofDays(10)));
        auction.setId(1);
        String server = "http://localhost:" + puerto + "/api/auction/"+auction.getId();
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<AuctionDTO> auctionDTOHttpEntity = new HttpEntity<>(auctionDTO);
        ResponseEntity<Auction> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
                Auction.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(before.plus(Period.ofDays(10)),response.getBody().getDate());

    }

    @Test
    void updateInexistentAuction(){
        AuctionDTO auctionDTO = new AuctionDTO();
        auction.setId(1);
        String server = "http://localhost:" + puerto + "/api/auction/"+(auction.getId()+1000);
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<AuctionDTO> auctionDTOHttpEntity = new HttpEntity<>(auctionDTO);
        ResponseEntity<Auction> response = testRestTemplate.exchange(server, HttpMethod.PATCH, auctionDTOHttpEntity,
                Auction.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    //--------------
    //Borrar remates
    @Test
    void deleteNotFinishedAuction(){
        auction.setId(1);
        String server = "http://localhost:" + puerto + "/api/auction/"+auction.getId();
        HttpEntity<Auction> auctionDTOHttpEntity = new HttpEntity<>(auction);
        ResponseEntity<Auction> response = testRestTemplate.exchange(server, HttpMethod.DELETE, auctionDTOHttpEntity,
                Auction.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getDeleted(), true);
    }

    @Test
    void deleteFinishedAuction(){
        //Seter en true el atributo finished en la BD del auction cuyo id es 2.
        auction.setId(2);
        String server = "http://localhost:" + puerto + "/api/auction/"+auction.getId();
        HttpEntity<Auction> auctionDTOHttpEntity = new HttpEntity<>(auction);
        ResponseEntity<Auction> response = testRestTemplate.exchange(server, HttpMethod.DELETE, auctionDTOHttpEntity,
                Auction.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteInexistentAuction(){
        auction.setId(2);
        String server = "http://localhost:" + puerto + "/api/auction/"+(auction.getId()+10000);
        HttpEntity<Auction> auctionDTOHttpEntity = new HttpEntity<>(auction);
        ResponseEntity<Auction> response = testRestTemplate.exchange(server, HttpMethod.DELETE, auctionDTOHttpEntity,
                Auction.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void getFirst10Auctions(){
        String server = "http://localhost:" + puerto + "/api/auction?page=0&limit=10";

        ResponseEntity<String> response = testRestTemplate.getForEntity(server, String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            AuctionPageDTO auctionPageDTO = objectMapper.readValue(response.getBody(), AuctionPageDTO.class);
            assertEquals(auctionPageDTO.getContent().size(),10);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

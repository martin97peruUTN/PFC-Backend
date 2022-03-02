package pfc.consignacionhacienda.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pfc.consignacionhacienda.dao.AuctionDAO;
import pfc.consignacionhacienda.dto.AuctionDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.Locality;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.locality.LocalityService;
import pfc.consignacionhacienda.services.user.UserService;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuctionServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(AuctionServiceImplTest.class);
    @Autowired
    private AuctionService auctionService;

    @Autowired
    private LocalityService localityService;

    @SpyBean
    private UserService userService;

    @Mock
    Collection<? extends GrantedAuthority> list2;

    @MockBean
    AuctionDAO auctionDAO;

    private Auction auction;
    private Locality locality;
    private List<GrantedAuthority> roles;

    @BeforeEach
    void initTests(){
        roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("Administrador"));
        when(list2.toArray()).thenReturn(roles.toArray());
        Mockito.doReturn(list2).when(userService).getCurrentUserAuthorities();

        User user = new User();
        user.setId(1);
        user.setPassword("$2a$10$.K6U/unji7nI/Xvqfj5Z7efTBTN9/xbGuNj1n96d2ZCeANpJqR2uC");
        user.setName("testUser");
        user.setUsername("test");
        user.setRol("Administrador");
        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        Mockito.doReturn(user).when(userService).getCurrentUser();
        auction = new Auction();
        auction.setId(1);
        auction.setDeleted(false);
        auction.setFinished(false);
        locality = new Locality();
        locality.setDeleted(false);
        locality.setName("San Justo");
        locality.setId(1);
        auction.setLocality(locality);
        auction.setSenasaNumber("aNumber");
        auction.setDate(Instant.now().plus(Period.ofDays(10)));
        auction.setUsers(users);

        when(auctionDAO.save(any(Auction.class))).thenReturn(auction);
        Optional<Auction> auctionOpt = Optional.of(auction);
        when(auctionDAO.findById(any(Integer.class))).thenReturn(auctionOpt);
    }

    //Crear remates
    @Test
    void createAuctionSuccesfully(){
        Auction auction1 = new Auction();
        auction1.setUsers(auction.getUsers());
        auction1.setDate(auction.getDate());
        auction1.setSenasaNumber(auction.getSenasaNumber());
        auction1.setLocality(auction.getLocality());
        auction1.setDeleted(auction.getDeleted());
        auction1.setFinished(auction.getFinished());
        auction1.setId(null);
        Auction auction2 = new Auction();
        auction2.setUsers(auction.getUsers());
        auction2.setDate(auction.getDate());
        auction2.setSenasaNumber(auction.getSenasaNumber());
        auction2.setLocality(auction.getLocality());
        auction2.setDeleted(auction.getDeleted());
        auction2.setFinished(auction.getFinished());
        auction2.setId(1);
        when(auctionDAO.save(any(Auction.class))).thenReturn(auction2);
//      auction1.setLocality(locality);
        AtomicReference<Auction> auctionSaved = new AtomicReference<>();
        assertDoesNotThrow(() -> auctionSaved.set(auctionService.saveAuction(auction1)));
        assertEquals(auctionSaved.get().getId(), 1);
//      assertDoesNotThrow(()->auctionService.getAuctionById(auctionSaved.getId()));
    }

    @Test
    void createAuctionWithInvalidDate(){
        auction.setDate(Instant.now().minus(Period.ofDays(10)));
        assertThrows(InvalidCredentialsException.class, ()->auctionService.saveAuction(auction));
    }

    //-----------------
    //Actualizar remates
    @Test
    void updateAuctionSuccesfully(){
        AuctionDTO auctionDTO = new AuctionDTO();

//            Auction auction = auctionService.getAuctionById(1);
        assertEquals(auction.getId(),1);
        assertEquals(auction.getLocality().getId(),1);
        locality.setId(2);
        locality.setName("Santa Fe");
        auctionDTO.setLocality(locality);
        assertDoesNotThrow(() -> auction = auctionService.updateAuctionById(1,auctionDTO));
        assertEquals(auction.getId(),1);
        assertEquals(auction.getLocality().getId(),2);
    }

    @Test
    void updateAuctionWithInvalidDate(){
        AuctionDTO auctionDTO = new AuctionDTO();
        assertEquals(auction.getId(),1);
        assertEquals(auction.getLocality().getId(),1);
        auctionDTO.setDate(Instant.now().minus(Period.ofDays(10)));
        assertThrows( InvalidCredentialsException.class,()->auctionService.updateAuctionById(1,auctionDTO));
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
        assertThrows( InvalidCredentialsException.class,()->auctionService.updateAuctionById(1,auctionDTO));
    }

    @Test
    void updateAuctionWithValidDate(){
        AuctionDTO auctionDTO = new AuctionDTO();
        assertEquals(auction.getId(),1);
        assertEquals(auction.getLocality().getId(),1);
        Instant before = auction.getDate();
        auctionDTO.setDate(auction.getDate().plus(Period.ofDays(10)));
        assertDoesNotThrow(() -> auction = auctionService.updateAuctionById(1,auctionDTO));
        assertNotEquals(before, auction.getDate());
        assertEquals(before.plus(Period.ofDays(10)),auction.getDate());
    }

    @Test
    void updateInexistentAuction(){
        AuctionDTO auctionDTO = new AuctionDTO();
        auctionDTO.setDate(Instant.now().plus(Period.ofDays(10)));
        when(auctionDAO.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(AuctionNotFoundException.class,()-> auctionService.updateAuctionById(100,auctionDTO));
    }

    //--------------
    //Borrar remates
    @Test
    void deleteNotFinishedAuction(){
        assertEquals(auction.getDeleted(), false);
        assertDoesNotThrow(() -> auction = auctionService.deleteAuctionById(auction.getId()));
        assertEquals(auction.getDeleted(), true);
    }

    @Test
    void deleteFinishedAuction(){
        auction.setFinished(true);
        assertThrows(HttpForbidenException.class,()->auctionService.deleteAuctionById(auction.getId()));
    }

    @Test
    void deleteInexistentAuction(){
        when(auctionDAO.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(AuctionNotFoundException.class,()->auctionService.deleteAuctionById(auction.getId()));
    }
}

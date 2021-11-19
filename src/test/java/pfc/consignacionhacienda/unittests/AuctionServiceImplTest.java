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
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.locality.LocalityNotFoundException;
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

        User u = userService.findUserById(1);
        ArrayList<User> users = new ArrayList<>();
        users.add(u);
        Mockito.doReturn(u).when(userService).getCurrentUser();
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
        logger.debug(String.valueOf(auctionOpt.toString()));
        when(auctionDAO.findById(any(Integer.class))).thenReturn(auctionOpt);
//        when(auctionDAO.save(any(Auction.class))).thenReturn(auction);
//        AuctionDTO auctionDTO = new AuctionDTO();
//        try {
//            auctionDTO.setLocality(localityService.getLocalityById(1));
//            auctionService.updateAuctionById(1,auctionDTO);
//        } catch (AuctionNotFoundException | HttpUnauthorizedException | LocalityNotFoundException e) {
//            e.printStackTrace();
//        }

    }

    //Crear remates
    @Test
    void createAuctionSuccesfully(){
//        when(auctionDAO.save(any(Auction.class))).thenReturn(auction);
//        Optional<Auction> auctionOpt = Optional.of(auction);
//        logger.debug(String.valueOf(auctionOpt.toString()));
//        when(auctionDAO.findById(any(Integer.class))).thenReturn(auctionOpt);

        Auction auction1 = new Auction();
        auction1.setUsers(auction.getUsers());
        auction1.setDate(auction.getDate());
        auction1.setSenasaNumber(auction.getSenasaNumber());
        auction1.setLocality(auction.getLocality());
        auction1.setDeleted(auction.getDeleted());
        auction1.setFinished(auction.getFinished());
        auction1.setId(null);
        logger.debug(String.valueOf(auction.getId()));
//        auction1.setLocality(locality);
        try {
            Auction auctionSaved = auctionService.saveAuction(auction1);
            assertEquals(auctionSaved.getId(),1);
//            assertDoesNotThrow(()->auctionService.getAuctionById(auctionSaved.getId()));
        } catch (HttpUnauthorizedException e) {
            e.printStackTrace();
        }
    }

    //Esto es integracion
    @Test
    void createAuctionWithoutLocality(){

//        auction = new Auction();
//        auction.setSenasaNumber("oneNumber");
//        auction.setDate(Instant.now().plus(Period.ofDays(10)));
//        auction.setLocality(null);
//        when(auctionDAO.save(any(Auction.class))).thenReturn(auction);
//        ArrayList<User> users = new ArrayList<>();
//        users.add(userService.findUserById(1));
//        auction.setUsers(users);
//        //            Auction auctionSaved = auctionService.saveAuction(auction);
//        assertThrows(Exception.class, ()->auctionService.saveAuction(auction));
    }

    @Test
    void createAuctionWithoutUser(){

        auction.setUsers(null);
        when(auctionDAO.save(any(Auction.class))).thenReturn(auction);
        try {
            auction.setLocality(localityService.getLocalityById(1));
        } catch (LocalityNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Auction auctionSaved = auctionService.saveAuction(auction);
            assertDoesNotThrow(()->auctionService.getAuctionById(auctionSaved.getId()));//porque esto se valida en el RestController
        } catch (HttpUnauthorizedException e) {
            e.printStackTrace();
        }
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
        try {
//            Auction auction = auctionService.getAuctionById(1);
            assertEquals(auction.getId(),1);
            assertEquals(auction.getLocality().getId(),1);
            locality.setId(2);
            locality.setName("Santa Fe");
            auctionDTO.setLocality(locality);
            logger.debug(auction.toString());
            auction = auctionService.updateAuctionById(1,auctionDTO);
            logger.debug(auction.toString());
            assertEquals(auction.getId(),1);
            assertEquals(auction.getLocality().getId(),2);
        } catch (HttpUnauthorizedException | AuctionNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateAuctionWithInvalidDate(){
        AuctionDTO auctionDTO = new AuctionDTO();

//        Auction auction = null;
//        try {
//            auction = auctionService.getAuctionById(1);
//        } catch (AuctionNotFoundException e) {
//            e.printStackTrace();
//        }
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

//        Auction auction = null;
//        try {
//            auction = auctionService.getAuctionById(1);
//        } catch (AuctionNotFoundException e) {
//            e.printStackTrace();
//        }
        assertEquals(auction.getId(),1);
        assertEquals(auction.getLocality().getId(),1);
        Instant before = auction.getDate();
        auctionDTO.setDate(auction.getDate().plus(Period.ofDays(10)));
        try {
            auction = auctionService.updateAuctionById(1,auctionDTO);
            assertNotEquals(before, auction.getDate());
            assertEquals(before.plus(Period.ofDays(10)),auction.getDate());
        } catch (AuctionNotFoundException | HttpUnauthorizedException e) {
            e.printStackTrace();
        }
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
        logger.debug(auction.toString());
        assertEquals(auction.getDeleted(), false);
        try {
            auction = auctionService.deleteAuctionById(auction.getId());
        } catch (AuctionNotFoundException | HttpForbidenException | HttpUnauthorizedException e) {
            e.printStackTrace();
        }
        assertEquals(auction.getDeleted(), true);
    }

    @Test
    void deleteFinishedAuction(){
        logger.debug(auction.toString());
        auction.setFinished(true);
        assertThrows(HttpForbidenException.class,()->auctionService.deleteAuctionById(auction.getId()));
    }

    @Test
    void deleteInexistentAuction(){
        logger.debug(auction.toString());
        when(auctionDAO.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThrows(AuctionNotFoundException.class,()->auctionService.deleteAuctionById(auction.getId()));
    }
}

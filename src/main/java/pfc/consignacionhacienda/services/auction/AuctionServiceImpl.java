package pfc.consignacionhacienda.services.auction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.AuctionDAO;
import pfc.consignacionhacienda.dto.AuctionDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.AuctionMapper;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService{

    private static final Logger logger = LoggerFactory.getLogger(AuctionServiceImpl.class);

    @Autowired
    private AuctionDAO auctionDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionMapper auctionMapper;

    @Override
    public Auction saveAuction(Auction auction) throws InvalidCredentialsException, HttpUnauthorizedException{
        logger.debug(Instant.now().plus(Period.ofDays(1)).toString());
        if(Instant.now().isAfter(auction.getDate())){
            throw new InvalidCredentialsException("La fecha del remate es invalida");
        }
        logger.debug(userService.getCurrentUserAuthorities().toArray()[0].toString());
        if(!userService.getCurrentUserAuthorities().toArray()[0].toString().equals("Administrador")) {
            Optional<User> aux = auction.getUsers().stream().filter(user -> user.getId().equals(userService.getCurrentUser().getId())).findFirst();
            if (aux.isEmpty()) {
                throw new HttpUnauthorizedException("Usted no tiene acceso a este remate.");
            }
        }
//        auction.setFinished(false);
        return auctionDAO.save(auction);
    }

    @Override
    public List<Auction> getAllAuctions() {
        return auctionDAO.findAll();
    }

    @Override
    public Page<Auction> getAllAuctionsByPage(Integer page, Integer limit) throws InvalidCredentialsException {
        if(page<0 || limit<0){
            throw new InvalidCredentialsException("Parametros invalidos.");
        }
        return auctionDAO.findAll(PageRequest.of(page, limit));
    }

    @Override
    public List<Auction> getAllNotDeletedAuctions() {
        return auctionDAO.findByDeletedNullOrDeletedFalse();
    }

    @Override
    public Page<Auction> getAllNotDeletedAuctionsByPage(Integer page, Integer limit) throws InvalidCredentialsException {
        if(page<0 || limit<0){
            throw new InvalidCredentialsException("Parametros invalidos.");
        }
        return auctionDAO.findByDeletedNullOrDeletedFalse(PageRequest.of(page, limit));
    }

    @Override
    public Page<Auction> getOwnNotDeletedAuctionsByPageAndId(Integer id, Integer page, Integer limit) throws InvalidCredentialsException, HttpUnauthorizedException {
        if(page<0 || limit<0){
            throw new InvalidCredentialsException("Parametros invalidos.");
        }
        if(id!=userService.getCurrentUser().getId()){
            throw new HttpUnauthorizedException("El usuarion no coincide");
        }
        return auctionDAO.findOwnById(id, PageRequest.of(page, limit));
    }

    @Override
    public Page<Auction> getOthersNotDeletedAuctionsByPageAndId(Integer id, Integer page, Integer limit) throws InvalidCredentialsException, HttpUnauthorizedException {
        if(page<0 || limit<0){
            throw new InvalidCredentialsException("Parametros invalidos.");
        }
        if(id!=userService.getCurrentUser().getId()){
            throw new HttpUnauthorizedException("El usuarion no coincide");
        }
        return auctionDAO.findOthersById(id, PageRequest.of(page, limit));
    }

    @Override
    public Page<Auction> getAllNotDeletedAndNotFinishedAuctionsByPage(Integer page, Integer limit) throws InvalidCredentialsException {
        if(page<0 || limit<0){
            throw new InvalidCredentialsException("Parametros invalidos.");
        }
        return auctionDAO.findAllAdmin(PageRequest.of(page, limit));
    }

    @Override
    public Auction getAuctionById(Integer id) throws AuctionNotFoundException {
        Optional<Auction> auctionOpt = auctionDAO.findById(id);
        if(auctionOpt.isPresent()){
            return auctionOpt.get();
        }
        throw new AuctionNotFoundException("No existe un remate con id: " + id);
    }

    @Override
    public Auction deleteAuctionById(Integer id) throws AuctionNotFoundException, HttpUnauthorizedException, HttpForbidenException {
        Auction auction = getAuctionById(id);
        if(auction.getFinished() != null && auction.getFinished()){
            throw new HttpForbidenException("No puede eliminarse un remate que ya se realizo.");
        }
        auction.setDeleted(true);
        return this.saveAuction(auction);
    }

    @Override
    public Auction updateAuctionById(Integer id, AuctionDTO fields) throws AuctionNotFoundException, InvalidCredentialsException, HttpUnauthorizedException {

        if(fields.getId() != null){
            if(!fields.getId().equals(id)){
                throw new InvalidCredentialsException("No se puede modificar el id del remate");
            }
        }

        if(fields.getUsers() != null){
            logger.debug((fields.getUsers().get(0)).toString());
            List<User> users = new ArrayList<>();
            for(User u: fields.getUsers()){
                User aux = u;
                if(aux.getRol() == null){
                    //Implemente este for porque tal vez, en el body de la request solo vienen los id de usuarios, entonces u.getROl() era nulo.
                    aux = userService.findUserById(aux.getId());
                }
                if(aux.getRol().equals("Consignatario") || aux.getRol().equals("Administrador")){
                    users.add(aux);
                }
            }
            if(users.isEmpty()){
                throw new InvalidCredentialsException("Debe existir al menos un usuario Consignatario o Administrador asociado al remate.");
            }
        }

        Auction auction = getAuctionById(id);
        auctionMapper.updateAuctionFromDto(fields, auction);
        return saveAuction(auction);
    }
}

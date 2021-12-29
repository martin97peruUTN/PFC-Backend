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
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.*;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.notSoldBatch.NotSoldBatchService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;
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

    @Autowired
    private BatchService batchService;

    @Autowired
    private SoldBatchService soldBatchService;

    @Autowired
    private NotSoldBatchService notSoldBatchService;

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
        return auctionDAO.save(auction);
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
        if (fields.getDate() != null && Instant.now().isAfter(fields.getDate())) {
            throw new InvalidCredentialsException("La fecha del remate es invalida");
        }
        Auction auction = getAuctionById(id);
        auctionMapper.updateAuctionFromDto(fields, auction);
        return auctionDAO.save(auction);
    }

    @Override
    public List<User> getUsersByAuctionId(Integer auctionID) throws AuctionNotFoundException {
        Optional<Auction> auction = auctionDAO.findById(auctionID);
        if(auction.isPresent()){
            return auction.get().getUsers();
        }
        throw new AuctionNotFoundException("El remate con id: " + auctionID + " no existe");
    }

    @Override
    public Auction removeUserFromAuction(Integer auctionId, Integer userId) throws AuctionNotFoundException, UserNotFoundException, HttpForbidenException, HttpUnauthorizedException {
        Optional<Auction> auctionOpt = auctionDAO.findById(auctionId);
        if(auctionOpt.isPresent()){
            Auction auction = auctionOpt.get();
            List<User> autionUserList = auction.getUsers();
            Optional<User> userOptional = autionUserList.stream().filter(u -> u.getId().equals(userService.getCurrentUser().getId())).findFirst();
            if(!userOptional.isPresent() && !userService.getCurrentUserAuthorities().toArray()[0].equals("Administrador")){
                throw new HttpUnauthorizedException("Usted no es participante del remate que quiere modificar");
            }
            Optional<User> userOpt = auction.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst();
            if(userOpt.isPresent()){
                if(!userOpt.get().getId().equals(userService.getCurrentUser().getId())){
                    auction.getUsers().remove(userOpt.get());
                    return auctionDAO.save(auction);
                }
                throw new HttpForbidenException("No puede eliminarse a si mismo");
            }
            throw new UserNotFoundException("El usuario con id: " + userId + " no participa en este remate.");
        }
        throw new AuctionNotFoundException("El remate con id: " + auctionId + " no existe.");
    }

    @Override
    public Auction addUserToAuction(Integer auctionId, Integer userId) throws AuctionNotFoundException, UserNotFoundException, HttpUnauthorizedException {
        User user = userService.findUserById(userId);
        Optional<Auction> auctionOpt = auctionDAO.findById(auctionId);
        if(auctionOpt.isPresent()){
            Auction auction = auctionOpt.get();
            List<User> autionUserList = auction.getUsers();
            Optional<User> userOptional = autionUserList.stream().filter(u -> u.getId().equals(userService.getCurrentUser().getId())).findFirst();
            if(userOptional.isPresent() || userService.getCurrentUserAuthorities().toArray()[0].equals("Administrador")){
                if(!auction.getUsers().contains(user)){
                    auction.getUsers().add(user);
                    return auctionDAO.save(auction);
                }
                return auction;
            }
            throw new HttpUnauthorizedException("Usted no es participante del remate que quiere modificar");
        }
        throw new AuctionNotFoundException("El remate con id: " + auctionId + " no existe");
    }

    @Override
    public Auction finishAuctionById(Integer id) throws AuctionNotFoundException, HttpUnauthorizedException {
        Auction thisAuction = getAuctionById(id);
        if(thisAuction.getDeleted()!=null && thisAuction.getDeleted()){
            throw new AuctionNotFoundException("No existe remate con id "+id);
        }
        if(!userService.getCurrentUserAuthorities().toArray()[0].toString().equals("Administrador")) {
            boolean userBelongsToAuction = thisAuction.getUsers().stream().anyMatch(u -> u.getId().equals(userService.getCurrentUser().getId()));
            if (!userBelongsToAuction) {
                throw new HttpUnauthorizedException("Usted no esta autorizado a editar este remate.");
            }
        }
        List<NotSoldBatch> notSoldBatches = new ArrayList<>();
        List<Batch> batchList = batchService.getBatchesByAuctionId(id);
        for(Batch b: batchList){
            for(AnimalsOnGround a: b.getAnimalsOnGround()){
                Integer amount = a.getAmount()-soldBatchService.getTotalSold(a.getId());
                if(!a.getSold()){
                    if(amount<=0){
                        throw new IllegalStateException("El animalOnGround no esta vendido pero no tiene cantidades disponibles para vender");
                    }
                    NotSoldBatch notSoldBatch = new NotSoldBatch();
                    notSoldBatch.setAnimalsOnGround(a);
                    notSoldBatch.setAmount(amount);
                    notSoldBatches.add(notSoldBatch);
                }
            }
        }
        notSoldBatchService.saveAll(notSoldBatches);
        thisAuction.setFinished(true);
        return auctionDAO.save(thisAuction);
    }

    @Override
    public List<NotSoldBatch> resumeAuctionById(Integer auctionId) throws AuctionNotFoundException, HttpUnauthorizedException {
        Auction thisAuction = getAuctionById(auctionId);
        if(thisAuction.getDeleted()!=null && thisAuction.getDeleted()){
            throw new AuctionNotFoundException("No existe remate con id "+auctionId);
        }
        if(!userService.getCurrentUserAuthorities().toArray()[0].toString().equals("Administrador")) {
            boolean userBelongsToAuction = thisAuction.getUsers().stream().anyMatch(u -> u.getId().equals(userService.getCurrentUser().getId()));
            if (!userBelongsToAuction) {
                throw new HttpUnauthorizedException("Usted no esta autorizado a editar este remate.");
            }
        }
        //Esta parte no es "atomica", no se que pasaria si se setea en false y no llega a eliminar los notSoldBatches
        thisAuction.setFinished(false);
        auctionDAO.save(thisAuction);
        return notSoldBatchService.deleteAllByAuctionId(auctionId);
    }
}

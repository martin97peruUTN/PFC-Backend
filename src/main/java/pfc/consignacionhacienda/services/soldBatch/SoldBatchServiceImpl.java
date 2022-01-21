package pfc.consignacionhacienda.services.soldBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.SoldBatchDAO;
import pfc.consignacionhacienda.dto.SoldBatchDTO;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.HttpUnauthorizedException;
import pfc.consignacionhacienda.exceptions.animalsOnGround.AnimalsOnGroundNotFound;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.exceptions.batch.BatchNotFoundException;
import pfc.consignacionhacienda.exceptions.soldBatch.SoldBatchNotFoundException;
import pfc.consignacionhacienda.model.AnimalsOnGround;
import pfc.consignacionhacienda.model.Auction;
import pfc.consignacionhacienda.model.Batch;
import pfc.consignacionhacienda.model.SoldBatch;
import pfc.consignacionhacienda.services.animalsOnGround.AnimalsOnGroundService;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.SoldBatchMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SoldBatchServiceImpl implements SoldBatchService{

    private static final Logger logger = LoggerFactory.getLogger(SoldBatchServiceImpl.class);

    @Autowired
    private SoldBatchDAO soldBatchDAO;

    @Autowired
    private AnimalsOnGroundService animalsOnGroundService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private SoldBatchMapper soldBatchMapper;

    @Autowired
    private ClientService clientService;

    @Override
    public Integer getTotalSold(Integer id) {
        Integer total = soldBatchDAO.getTotalSold(id);
        return total==null?0:total;
    }

    @Override
    public List<SoldBatch> findSoldBatchesNotDeletedByAnimalsOnGroundId(Integer id) {
        return soldBatchDAO.findSoldBatchesNotDeletedByAnimalsOnGroundId(id);
    }

    @Override
    public SoldBatch saveSoldBatch(SoldBatch soldBatch, Integer animalsOnGroundId) throws AnimalsOnGroundNotFound, HttpForbidenException, BatchNotFoundException, AuctionNotFoundException, HttpUnauthorizedException {
        AnimalsOnGround animalsOnGround = animalsOnGroundService.findByIdNotDeleted(animalsOnGroundId);
        Integer totalSold = this.getTotalSold(animalsOnGroundId);
        if(soldBatch.getAmount() == null){
            throw new IllegalArgumentException("El atributo 'cantidad' no puede ser nulo");
        }
        Batch batch = batchService.getBatchByAnimalsOnGroundId(animalsOnGroundId);
        if(batch == null || (batch.getDeleted() != null && batch.getDeleted())){
            throw new BatchNotFoundException("El Lote Vendido pertenece a un Lote de Venta inexistente");
        }
        Auction auction = batch.getAuction();
        if(auction.getDeleted() != null && auction.getDeleted()){
            throw new AuctionNotFoundException("El Lote Vendido pertenece a un Remate inexistente");
        }
        if(soldBatch.getAmount() <= 0){
            throw new HttpForbidenException("La cantidad de animales vendidos debe ser mayor a cero.");
        }
        if(soldBatch.getAmount()+totalSold > animalsOnGround.getAmount()){
            throw new HttpForbidenException("La cantidad de animales vendidos supera a la cantidad disponible para la venta.");
        }
        if(auction.getFinished() != null && auction.getFinished()){
            throw new HttpForbidenException("No puede editarse un remate finalizado .");
        }
        if(!userService.getCurrentUserAuthorities().toArray()[0].toString().equals("Administrador")) {
            boolean userBelongsToAuction = auction.getUsers().stream().anyMatch(u -> u.getId().equals(userService.getCurrentUser().getId()));
            if (!userBelongsToAuction) {
                throw new HttpUnauthorizedException("Usted no esta autorizado a editar este remate.");
            }
        }
        soldBatch.setAnimalsOnGround(animalsOnGround);
        SoldBatch soldBatchSaved = soldBatchDAO.save(soldBatch);
        totalSold = this.getTotalSold(animalsOnGroundId);
        if(totalSold.equals(animalsOnGround.getAmount())){
            animalsOnGround.setSold(true);
            animalsOnGroundService.save(animalsOnGround);
        }
        return soldBatchSaved;
    }

    @Override
    public SoldBatch updateSoldBatchById(SoldBatchDTO soldBatchDTO, Integer soldBatchId) throws SoldBatchNotFoundException, AnimalsOnGroundNotFound, IllegalArgumentException, BatchNotFoundException, AuctionNotFoundException, HttpForbidenException, HttpUnauthorizedException {
        if(soldBatchDTO.getId() != null && !soldBatchId.equals(soldBatchDTO.getId())){
            throw new IllegalArgumentException("Los id del path y del objeto a editar son distintos");
        }
        SoldBatch soldBatch = findByIdNotDeleted(soldBatchId);
        AnimalsOnGround animalsOnGround = animalsOnGroundService.findByIdNotDeleted(soldBatch.getAnimalsOnGround().getId());
        Batch batch = batchService.getBatchByAnimalsOnGroundId(animalsOnGround.getId());
        if(batch == null || (batch.getDeleted() != null && batch.getDeleted())){
            throw new BatchNotFoundException("El Lote Vendido pertenece a un Lote de Venta inexistente");
        }
        Auction auction = batch.getAuction();
        if(auction.getDeleted() != null && auction.getDeleted()){
            throw new AuctionNotFoundException("El Lote Vendido pertenece a un Remate inexistente");
        }

        if(auction.getFinished() != null && auction.getFinished()){
            throw new HttpForbidenException("No puede editarse un remate finalizado.");
        }

        if(!userService.getCurrentUserAuthorities().toArray()[0].toString().equals("Administrador")) {
            boolean userBelongsToAuction = auction.getUsers().stream().anyMatch(u -> u.getId().equals(userService.getCurrentUser().getId()));
            if (!userBelongsToAuction) {
                throw new HttpUnauthorizedException("Usted no esta autorizado a editar este remate.");
            }
        }
        if(soldBatchDTO.getAmount() != null){
            if(soldBatchDTO.getAmount() <= 0){
                throw new HttpForbidenException("La cantidad de animales vendidos debe ser mayor a cero.");
            }
            //Al la cantidad de AnimalsOnGround vendidos le resto el del lote que estoy editando, para volver a verificar la cantidad disponible
            Integer totalSold = this.getTotalSold(animalsOnGround.getId()) - soldBatch.getAmount();
            if(soldBatchDTO.getAmount()+totalSold > animalsOnGround.getAmount()){
                throw new HttpForbidenException("La cantidad de animales vendidos supera a la cantidad disponible para la venta.");
            }
            if(soldBatchDTO.getAmount()+totalSold == animalsOnGround.getAmount()){
                //setear como vendido el animalsonground
                if(animalsOnGround.getSold() != null && !animalsOnGround.getSold()){
                    animalsOnGround.setSold(true);
                    animalsOnGroundService.save(animalsOnGround);
                }
            }else{
                //setear como false el vendido de animalsonground
                if(animalsOnGround.getSold() != null && animalsOnGround.getSold()){
                    animalsOnGround.setSold(false);
                    animalsOnGroundService.save(animalsOnGround);
                }
            }
        }
        soldBatchMapper.updateSoldBatchFromDto(soldBatchDTO, soldBatch);
        return soldBatchDAO.save(soldBatch);
    }

    @Override
    public Page<SoldBatchResponseDTO> getSoldBatchsByAuctionAndPage(Integer auctionId, Integer page, Integer limit) {
        Pageable p = PageRequest.of(page, limit);
        Page<SoldBatch> soldBatches = soldBatchDAO.findByAuctionId(auctionId, p);
        List<SoldBatchResponseDTO> responseDTOList = new ArrayList<>();
        for(SoldBatch soldBatch: soldBatches){
            SoldBatchResponseDTO soldBatchResponseDTO = new SoldBatchResponseDTO();
            soldBatchResponseDTO.setId(soldBatch.getId());
            soldBatchResponseDTO.setAmount(soldBatch.getAmount());
            soldBatchResponseDTO.setPrice(soldBatch.getPrice());
            soldBatchResponseDTO.setDteNumber(soldBatch.getDteNumber());
            soldBatchResponseDTO.setMustWeigh(soldBatch.getMustWeigh());
            soldBatchResponseDTO.setWeight(soldBatch.getWeight());
            soldBatchResponseDTO.setCategory(soldBatch.getAnimalsOnGround().getCategory());
            soldBatchResponseDTO.setBuyer(soldBatch.getClient());
            soldBatchResponseDTO.setSeller(clientService.findByProvenanceId(batchService.getBatchByAnimalsOnGroundId(soldBatch.getAnimalsOnGround().getId()).getProvenance().getId()));
            responseDTOList.add(soldBatchResponseDTO);
        }
        return new PageImpl<>(responseDTOList, p, soldBatches.getTotalElements());
    }

    @Override
    public SoldBatch deleteById(Integer soldBatchId) throws HttpUnauthorizedException, AnimalsOnGroundNotFound, SoldBatchNotFoundException, AuctionNotFoundException, HttpForbidenException, BatchNotFoundException {
        SoldBatch soldBatch = findByIdNotDeleted(soldBatchId);
        AnimalsOnGround animalsOnGround = animalsOnGroundService.findByIdNotDeleted(soldBatch.getAnimalsOnGround().getId());
        Batch batch = batchService.getBatchByAnimalsOnGroundId(animalsOnGround.getId());
        if(batch == null || (batch.getDeleted() != null && batch.getDeleted())){
            throw new BatchNotFoundException("El Lote Vendido pertenece a un Lote de Venta inexistente");
        }
        Auction auction = batch.getAuction();
        if(auction.getDeleted() != null && auction.getDeleted()){
            throw new AuctionNotFoundException("El Lote Vendido pertenece a un Remate inexistente");
        }

        if(auction.getFinished() != null && auction.getFinished()){
            throw new HttpForbidenException("No puede editarse un remate finalizado.");
        }
        if(!userService.getCurrentUserAuthorities().toArray()[0].toString().equals("Administrador")) {
            boolean userBelongsToAuction = auction.getUsers().stream().anyMatch(u -> u.getId().equals(userService.getCurrentUser().getId()));
            if (!userBelongsToAuction) {
                throw new HttpUnauthorizedException("Usted no esta autorizado a editar este remate.");
            }
        }
        animalsOnGround.setSold(false);
        animalsOnGroundService.save(animalsOnGround);
        soldBatchDAO.deleteById(soldBatchId);
        return soldBatch;
    }

    private SoldBatch findByIdNotDeleted(Integer soldBatchId) throws SoldBatchNotFoundException {
        Optional<SoldBatch> soldBatchOpt = soldBatchDAO.findById(soldBatchId);
        if(soldBatchOpt.isPresent()){
            SoldBatch soldBatch = soldBatchOpt.get();
            if(soldBatch.getDeleted() != null && soldBatch.getDeleted()){
                throw new SoldBatchNotFoundException("El LoteVendido con id: " + soldBatchId + " no existe.");
            }
            return soldBatch;
        }
        throw new SoldBatchNotFoundException("El LoteVendido con id: " + soldBatchId + " no existe.");
    }
}

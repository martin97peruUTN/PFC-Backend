package pfc.consignacionhacienda.services.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dto.SoldBatchResponseDTO;
import pfc.consignacionhacienda.exceptions.auction.AuctionNotFoundException;
import pfc.consignacionhacienda.model.*;
import pfc.consignacionhacienda.reports.dto.*;
import pfc.consignacionhacienda.services.auction.AuctionService;
import pfc.consignacionhacienda.services.batch.BatchService;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.services.notSoldBatch.NotSoldBatchService;
import pfc.consignacionhacienda.services.soldBatch.SoldBatchService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService{

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private BatchService batchService;

    @Autowired
    private SoldBatchService soldBatchService;

    @Autowired
    private NotSoldBatchService notsoldBatchService;

    @Autowired
    private ClientService clientService;

    @Override
    public Report getReportByAuctionId(Integer auctionId, Boolean withCategoryList) throws AuctionNotFoundException {
        Auction auction = auctionService.getAuctionById(auctionId);
        if(auction.getDeleted() != null && auction.getDeleted()){
            throw new AuctionNotFoundException("El remate con id: " + auctionId + " no existe.");
        }

        //Inicializacion de variables importantes.
        List<Consignee> consigneeList = new ArrayList<>();
        List<Assistant> assistantList = new ArrayList<>();
        Map<Integer, Seller> sellers = new LinkedHashMap<>();
        Map<Integer, Buyer> buyers = new LinkedHashMap<>();
        Report report = new Report();

        //Listado de todos los Batch de un remate
        List<Batch> batchList = batchService.getBatchesByAuctionId(auctionId);

        //Lista de consignatarios y asistentes
        for(User u: auction.getUsers()) {
            if(u.getRol().equals("Consignatario")){
                Consignee c = new Consignee();
                c.setName(u.getName() + " " + u.getLastname());
                consigneeList.add(c);
            } else {
                if(u.getRol().equals("Asistente")){
                    Assistant a = new Assistant();
                    a.setName(u.getName() + " " + u.getLastname());
                    assistantList.add(a);
                }
            }
        }

        //Generar info general del remate
        GeneralInfo generalInfo = this.getGeneralInfo(auction, consigneeList, assistantList, batchList);
        //Completamos los datos de la info general
        this.completeGeneralInfo(auctionId, sellers, buyers, batchList, generalInfo);
        report.setGeneralInfo(generalInfo);

        if(withCategoryList) {
            //Generar info de cada categoria de animal que participo en el remate
            Map<Integer, CommonInfo> categoryList = getCategoryListInfo(auctionId, batchList);
            report.setCategoryList(new ArrayList<>(categoryList.values()));
        }

        return report;
    }

    //Seteamos la info general del remate (fecha, lugar, nro senasa, lista de participantes, cantida de lotes entrados)
    private GeneralInfo getGeneralInfo(Auction auction, List<Consignee> consigneeList, List<Assistant> assistantList, List<Batch> batchList) {
        GeneralInfo generalInfo = new GeneralInfo();
        generalInfo.setDate(auction.getDate());
        generalInfo.setLocality(auction.getLocality().getName());
        generalInfo.setSenasaNumber(auction.getSenasaNumber());
        generalInfo.setConsignees(consigneeList);
        generalInfo.setAssistants(assistantList);

        //EL tamaño del listado es la cantidad de Batch que hubo para la venta.
        generalInfo.setTotalBatchesForSell(batchList.size());
        return generalInfo;
    }

    // Agregamos a la info general del remate info sobre cada vendedor, comprador,
    // 'cantidad de animales vendidos', 'cantidad de animales no vendidos'
    // y 'cantidad de lotes vendidos completamente'
    private void completeGeneralInfo(Integer auctionId, Map<Integer, Seller> sellers, Map<Integer, Buyer> buyers, List<Batch> batchList, GeneralInfo generalInfo) {
        CommonInfo commonInfo = new CommonInfo();
        Integer totalAnimalsNotSold = 0;
        Integer totalAnimalsSold = 0;
        Integer totalCompletelySoldBatches = 0;

        // Seteamos 'total de animales vendidos', 'total de animales no vendidos' y
        // 'total de lotes vendidos completamente' en el remate.
        //Recorremos cada Batch para obtener la info de CommonInfo General.
        for (Batch b: batchList){
            int contadorLotesVendidos = 0;
            for(AnimalsOnGround animalsOnGround: b.getAnimalsOnGround()) {
                totalAnimalsSold += soldBatchService.getTotalSold(animalsOnGround.getId());
                Optional<NotSoldBatch> notSoldBatchOpt = notsoldBatchService.getNotSoldBatchesByAnimalsOnGroundId(animalsOnGround.getId());
                if (notSoldBatchOpt.isPresent()) {
                    totalAnimalsNotSold += notSoldBatchOpt.get().getAmount();
                }
                else {
                    //Esto es si el remate no se termino, entonces no existe el NotSoldBatch correspondiente.
                    int aux = 0;
                    for(SoldBatch sb: soldBatchService.findSoldBatchesNotDeletedByAnimalsOnGroundId(animalsOnGround.getId())) {
                        aux += sb.getAmount();
                    }
                    totalAnimalsNotSold += animalsOnGround.getAmount() - aux;
                }
                //por cada animals on ground vendido, sumamos el contador. Si el contador es igual la cantidad
                // de la lista animals on ground del lote, este lote esta completamente vendido.
                if(animalsOnGround.getSold() != null && animalsOnGround.getSold()){
                    contadorLotesVendidos++;
                }
            }
            if(contadorLotesVendidos == b.getAnimalsOnGround().size()){
                totalCompletelySoldBatches++;
            }
        }

        //TODO completamos la info de cada Vendedor y Comprador del remate, con los LotesVendidos.
        double totalMoneyIncome = 0;// total dinero obtenido en el remate

        //totalMoneyIncome se pasa como copia entonces lo retornamos, pero este metodo
        // tambien modifica info de vendedores y compradores
        totalMoneyIncome = getTotalMoneyIncome(auctionId, sellers, buyers, totalMoneyIncome);

        //TODO IMPORTANTE! aqui lo que se hace es completar los datos de los animales que no se han vendido ya que
        // anteriormente recorrimos SoldBatch pero puede existir casos en los que no se haya vendido ni una
        // sola cabeza de un determinado AnimalsOnGround
        sellers.forEach((id, s) -> s.setTotalAnimalsNotSold(getAnimalsNotSoldBySellerId(id, auctionId)));

        //Completamos la info general del remate con los datos obtenidos.
        commonInfo.setBuyers(new ArrayList<>(buyers.values()));
        commonInfo.setSellers(new ArrayList<>(sellers.values()));
        commonInfo.setTotalAnimalsNotSold(totalAnimalsNotSold);
        commonInfo.setTotalAnimalsSold(totalAnimalsSold);
        commonInfo.setTotalMoneyIncome(totalMoneyIncome);
        generalInfo.setTotalBuyers(buyers.size());
        generalInfo.setTotalSeller(sellers.size());
        generalInfo.setTotalCompletelySoldBatches(totalCompletelySoldBatches);
        generalInfo.setCommonInfo(commonInfo);
    }

    private double getTotalMoneyIncome(Integer auctionId, Map<Integer, Seller> sellers, Map<Integer, Buyer> buyers, double totalMoneyIncome) {
        //Informacion sobre cada Vendedor y Comprador que participo en el remate
        for(SoldBatchResponseDTO sb: soldBatchService.getAllSoldBatchesByAuctionId(auctionId)){
            totalMoneyIncome += sb.getPrice();

            //TODO Aqui, por cada cliente comprador calculamos toda la informacion de lo que compro en el remate
            if(!buyers.containsKey(sb.getBuyer().getId())){
                Buyer buyer = new Buyer();
                buyer.setId(sb.getBuyer().getId());
                buyer.setName(sb.getBuyer().getName());
                buyer.setTotalBought(sb.getAmount());
                buyer.setTotalMoneyInvested(sb.getPrice());
                buyers.put(sb.getBuyer().getId(), buyer);
            } else {
                Buyer buyer = new Buyer();
                buyer.setId(sb.getBuyer().getId());
                buyer.setName(sb.getBuyer().getName());
                buyer.setTotalBought(buyers.get(sb.getBuyer().getId()).getTotalBought()+sb.getAmount());
                buyer.setTotalMoneyInvested(buyers.get(sb.getBuyer().getId()).getTotalMoneyInvested()+sb.getPrice());
                buyers.put(sb.getBuyer().getId(), buyer); //TODO cada comprador se correspondo a una unica posicion del map.
            }


            //TODO Aqui, por cada cliente vendedor calculamos toda la informacion de lo que vendio en el remate
            if(!sellers.containsKey(sb.getSeller().getId())) {
                Seller s = new Seller();
                s.setId(sb.getSeller().getId());
                s.setName(sb.getSeller().getName());
                s.setTotalAnimalsSold(sb.getAmount());
                s.setTotalMoneyIncome(sb.getPrice());
                sellers.put(sb.getSeller().getId(), s);
            } else {
                Seller s = new Seller();
                s.setId(sb.getSeller().getId());
                s.setName(sb.getSeller().getName());
                if(sellers.get(sb.getSeller().getId()).getTotalAnimalsSold() == null){
                    s.setTotalAnimalsSold(sb.getAmount());
                }else {
                    s.setTotalAnimalsSold(sellers.get(sb.getSeller().getId()).getTotalAnimalsSold() + sb.getAmount());
                }

                if (sellers.get(sb.getSeller().getId()).getTotalMoneyIncome() == null) {
                    s.setTotalMoneyIncome(sb.getPrice());
                } else {
                    s.setTotalMoneyIncome(sellers.get(sb.getSeller().getId()).getTotalMoneyIncome() + sb.getPrice());
                }
                s.setTotalAnimalsNotSold(sellers.get(sb.getSeller().getId()).getTotalAnimalsNotSold());
                sellers.put(sb.getSeller().getId(), s); //TODO cada vendedor se corresponde a una unica posicion del map
            }
        }
        return totalMoneyIncome;
    }


    //Animales no Vendidos por id de vendedor en un remate
    private Integer getAnimalsNotSoldBySellerId(Integer id, Integer auctionId) {
        //TODO todos los lotes ofrecido por un determinado vendedor, para saber cuantos quedaron sin vender.
        List<Batch> batchList = batchService.getBatchesByClientIdAndAuctionId(id, auctionId);
        Integer result = 0;
        for (Batch batch: batchList){
            for(AnimalsOnGround a: batch.getAnimalsOnGround()){
                Optional<NotSoldBatch> notSoldBatchOptional = notsoldBatchService.getNotSoldBatchesByAnimalsOnGroundId(a.getId());
                if(notSoldBatchOptional.isPresent()){
                    result += notSoldBatchOptional.get().getAmount();
                } else {
                    result += a.getAmount() - soldBatchService.getTotalSold(a.getId());
                }
            }
        }
        return result;
    }

    //-------------------------------------
    // Sobre la lista de categorias de animales
    private Map<Integer, CommonInfo> getCategoryListInfo(Integer auctionId, List<Batch> batchList) {
        //TODO este map contendra la info de cada category (CommonInfo) Integer es el id de cada categoria.
        Map<Integer, CommonInfo> categoryList = new LinkedHashMap<>();

        //TODO inicializacion de categoryList para evitar valores null (en la mayoria de los casos).
        for(Batch b: batchList){
            for(AnimalsOnGround ag: b.getAnimalsOnGround()) {
                CommonInfo commonInfo2 = new CommonInfo();
                commonInfo2.setSellers(new ArrayList<>());
                commonInfo2.setName(ag.getCategory().getName());
                commonInfo2.setBuyers(new ArrayList<>());
                commonInfo2.setTotalMoneyIncome(0d);
                commonInfo2.setTotalAnimalsNotSold(0);
                commonInfo2.setTotalAnimalsSold(0);
                if (!categoryList.containsKey(ag.getCategory().getId())) {
                    categoryList.put(ag.getCategory().getId(), commonInfo2);
                    //sellersInitial.put(ag.getCategory().getId(), List.of(s));
                }
            }
        }
        //TODO Seteamos en cad acategoria la info que se puede sacar de cada lote vendido.
        for (SoldBatchResponseDTO sb : soldBatchService.getSoldBatchesByAuctionAndPage(auctionId, 0, 10000)) {
            CommonInfo category = new CommonInfo();

            //TODO tanto en la lista de compradores como vendedores de una categoria. Agregamos valores
            // repetidos de los mismos en un unico listado. Luego los volvemos a filtrar y combinamos
            // los valores para obtener el resultado final
            if (!categoryList.containsKey(sb.getCategory().getId())) {
                category.setName(sb.getCategory().getName());
                category.setTotalMoneyIncome(sb.getPrice());
                category.setTotalAnimalsSold(sb.getAmount());
                Seller s = new Seller();
                s.setId(sb.getSeller().getId());
                s.setName(sb.getSeller().getName());
                s.setTotalAnimalsSold(sb.getAmount());
                s.setTotalMoneyIncome(sb.getPrice());
                category.setSellers(List.of(s));
                Buyer buyer = new Buyer();
                buyer.setId(sb.getBuyer().getId());
                buyer.setName(sb.getBuyer().getName());
                buyer.setTotalMoneyInvested(sb.getPrice());
                buyer.setTotalBought(sb.getAmount());
                category.setBuyers(List.of(buyer));
                categoryList.put(sb.getCategory().getId(), category);
            } else {
                category.setName(sb.getCategory().getName());
                CommonInfo aux = categoryList.get(sb.getCategory().getId());
                category.setTotalMoneyIncome(aux.getTotalMoneyIncome() + sb.getPrice());
                category.setTotalAnimalsSold(aux.getTotalAnimalsSold() + sb.getAmount());
                Seller s = new Seller();
                s.setId(sb.getSeller().getId());
                s.setName(sb.getSeller().getName());
                s.setTotalAnimalsSold(sb.getAmount());
                s.setTotalMoneyIncome(sb.getPrice());
                aux.getSellers().add(s);
                category.setSellers(aux.getSellers());
                Buyer buyer = new Buyer();
                buyer.setId(sb.getBuyer().getId());
                buyer.setName(sb.getBuyer().getName());
                buyer.setTotalMoneyInvested(sb.getPrice());
                buyer.setTotalBought(sb.getAmount());
                aux.getBuyers().add(buyer);
                category.setBuyers(aux.getBuyers());
                categoryList.put(sb.getCategory().getId(), category);
            }
        }

        // Setemaos datos de compradores y vendedores por cada categoria.
        categoryList.forEach((id, commonInfo1) -> {
            Map<Integer, Seller> sellerMap = new LinkedHashMap<>();
            Map<Integer, Buyer> buyerMap = new LinkedHashMap<>();
            for (Buyer b : commonInfo1.getBuyers()) {
                if (!buyerMap.containsKey(b.getId())) {
                    Buyer bAux = new Buyer();
                    bAux.setTotalBought(b.getTotalBought());
                    bAux.setId(b.getId());
                    bAux.setName(b.getName());
                    bAux.setTotalMoneyInvested(b.getTotalMoneyInvested());
                    buyerMap.put(bAux.getId(), bAux);
                } else {
                    Buyer bAnt = buyerMap.get(b.getId());
                    bAnt.setTotalBought(bAnt.getTotalBought() + b.getTotalBought());
//                    bAux.setName(b.getName());
                    bAnt.setTotalMoneyInvested(bAnt.getTotalMoneyInvested() + b.getTotalMoneyInvested());
                    buyerMap.put(bAnt.getId(), bAnt);
                }
            }
            for (Seller s : commonInfo1.getSellers()) {
                if (!sellerMap.containsKey(s.getId())) {
                    Seller sAux = new Seller();
                    sAux.setId(s.getId());
                    sAux.setName(s.getName());
                    sAux.setTotalMoneyIncome(s.getTotalMoneyIncome());
                    sAux.setTotalAnimalsSold(s.getTotalAnimalsSold());
                    sellerMap.put(sAux.getId(), sAux);
                } else {
                    Seller sAnt = sellerMap.get(s.getId());
                    sAnt.setTotalAnimalsSold(sAnt.getTotalAnimalsSold() + s.getTotalAnimalsSold());
//                    bAux.setName(b.getName());
                    sAnt.setTotalMoneyIncome(sAnt.getTotalMoneyIncome() + s.getTotalMoneyIncome());
                    sellerMap.put(sAnt.getId(), sAnt);
                }
            }
            commonInfo1.setSellers(new ArrayList<>(sellerMap.values()));
            commonInfo1.setBuyers(new ArrayList<>(buyerMap.values()));
            categoryList.put(id, commonInfo1);
        });

        //TODO Seteamos en cada categoria la cantidad de animales que quedaron sin vender (ademas de clientes que no vendieron ningun animal).
        getTotalNotSoldByCategory(auctionId, categoryList).forEach((id, totalNotSold) -> {
            if (categoryList.containsKey(id)) {
                CommonInfo aux = categoryList.get(id);
                aux.setTotalAnimalsNotSold(totalNotSold);
                categoryList.put(id, aux);
            } else {
                CommonInfo aux = new CommonInfo();
                aux.setTotalAnimalsNotSold(totalNotSold);
                categoryList.put(id, aux);
            }
        });
        return categoryList;
    }

    //Por cada Categoria participante en el remate, se calcula la cantidad de animales que no fueron vendidos,
    // y se agregan aquellos vendedores que no vendieron ni un animal de dicha categoria.
    private Map<Integer, Integer> getTotalNotSoldByCategory(Integer auctionId, Map<Integer, CommonInfo> categoryList) {
        Map<Integer, Integer> totalNotSoldByCategory = new LinkedHashMap<>();
        for(Batch b: batchService.getBatchesByAuctionId(auctionId)){
            Client seller = clientService.findByProvenanceId(b.getProvenance().getId());
            int idSeller = seller.getId();
            for(AnimalsOnGround ag: b.getAnimalsOnGround()){
                //TODO recorremos OTRA VEZ todos los Animales En Pista.
                int amount = 0;;
                Optional<NotSoldBatch> notSoldBatchOptional = notsoldBatchService.getNotSoldBatchesByAnimalsOnGroundId(ag.getId());
                //Si el remate no esta terminado, tal vez no existe notSoldBatch aun
                if(notSoldBatchOptional.isPresent()){
                    if(!totalNotSoldByCategory.containsKey(ag.getCategory().getId())){
                        amount = notSoldBatchOptional.get().getAmount();
                        totalNotSoldByCategory.put(ag.getCategory().getId(), amount);
                    } else {
                        amount = totalNotSoldByCategory.get(ag.getCategory().getId()) + notSoldBatchOptional.get().getAmount();
                        totalNotSoldByCategory.put(ag.getCategory().getId(), amount);
                    }
                } else {
                    //TODO Si no hay un NotSoldBatch asociado, a la cantidad de AnimalsOnGRound se le resta
                    // la cantidad que ya fue vendida.
                    if(!totalNotSoldByCategory.containsKey(ag.getCategory().getId())){
                        amount = ag.getAmount() - soldBatchService.getTotalSold(ag.getId());
                        totalNotSoldByCategory.put(ag.getCategory().getId(), amount);
                    } else {
                        amount = totalNotSoldByCategory.get(ag.getCategory().getId()) + (ag.getAmount() - soldBatchService.getTotalSold(ag.getId()));
                        totalNotSoldByCategory.put(ag.getCategory().getId(), amount);
                    }
                }
                //TODO agregamos aquellos vendedores que no vendieron aun animales.
                CommonInfo cAux = categoryList.get(ag.getCategory().getId());
                if(cAux.getSellers().stream().noneMatch(seller1 -> seller1.getId().equals(idSeller))){
                    Seller s = new Seller();
                    s.setId(idSeller);
                    s.setTotalMoneyIncome(0d);
                    s.setName(seller.getName());
                    cAux.getSellers().add(s);
                    categoryList.put(ag.getCategory().getId(), cAux);
                }

                //TODO la cantidad no vendidad se setea al vendedor correspondiente.
                int finalAmount = amount;
                categoryList.get(ag.getCategory().getId()).setSellers(
                        categoryList.get(ag.getCategory().getId()).getSellers().stream().map(c -> {
                            if(c.getId().equals(idSeller)) {
                                if(c.getTotalAnimalsNotSold() == null) {
                                    c.setTotalAnimalsNotSold(finalAmount);
                                } else {
                                    c.setTotalAnimalsNotSold(c.getTotalAnimalsNotSold() + finalAmount);
                                }
                            }
                            return c;
                        }).collect(Collectors.toList()));
            }
        }
        return totalNotSoldByCategory;
    }

}

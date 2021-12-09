package pfc.consignacionhacienda.utils;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import pfc.consignacionhacienda.model.Auction;

import java.util.List;
@JsonIncludeProperties(value = {"content"})
public class AuctionPageDTO {
    private List<Auction> content;

    public AuctionPageDTO(){

    }

    public List<Auction> getContent(){
        return content;
    }
    public void setContent(List<Auction> auctions){
        content = auctions;
    }
}

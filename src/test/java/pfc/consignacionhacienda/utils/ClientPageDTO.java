package pfc.consignacionhacienda.utils;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import pfc.consignacionhacienda.model.Client;

import java.util.List;

@JsonIncludeProperties(value = {"content"})
public class ClientPageDTO {
    private List<Client> content;

    public ClientPageDTO(){

    }

    public List<Client> getContent(){
        return content;
    }
    public void setContent(List<Client> clients){
        content = clients;
    }
}

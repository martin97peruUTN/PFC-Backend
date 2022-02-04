package pfc.consignacionhacienda.reports.dto;

import java.time.Instant;
import java.util.List;

public class GeneralInfo {

    private String locality;
    private Instant date;
    private String senasaNumber;
    private Integer totalSeller;
    private Integer totalBuyers;
    private Integer totalCompletelySoldBatches;
    private Integer totalBatchesForSell;
    private List<Consignee> consignees;
    private List<Assistant> assistants;
    private CommonInfo commonInfo;

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getSenasaNumber() {
        return senasaNumber;
    }

    public void setSenasaNumber(String senasaNumber) {
        this.senasaNumber = senasaNumber;
    }

    public Integer getTotalSeller() {
        return totalSeller;
    }

    public void setTotalSeller(Integer totalSeller) {
        this.totalSeller = totalSeller;
    }

    public Integer getTotalBuyers() {
        return totalBuyers;
    }

    public void setTotalBuyers(Integer totalBuyers) {
        this.totalBuyers = totalBuyers;
    }

    public List<Consignee> getConsignees() {
        return consignees;
    }

    public void setConsignees(List<Consignee> consignees) {
        this.consignees = consignees;
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }

    public void setAssistants(List<Assistant> assistants) {
        this.assistants = assistants;
    }

    public CommonInfo getCommonInfo() {
        return commonInfo;
    }

    public void setCommonInfo(CommonInfo commonInfo) {
        this.commonInfo = commonInfo;
    }

    public Integer getTotalCompletelySoldBatches() {
        return totalCompletelySoldBatches;
    }

    public void setTotalCompletelySoldBatches(Integer totalCompletelySoldBatches) {
        this.totalCompletelySoldBatches = totalCompletelySoldBatches;
    }

    public Integer getTotalBatchesForSell() {
        return totalBatchesForSell;
    }

    public void setTotalBatchesForSell(Integer totalBatchesForSell) {
        this.totalBatchesForSell = totalBatchesForSell;
    }

    @Override
    public String toString() {
        return "GeneralInfo{" +
                "locality='" + locality + '\'' +
                ", date=" + date +
                ", senasaNumber='" + senasaNumber + '\'' +
                ", totalSeller=" + totalSeller +
                ", totalBuyers=" + totalBuyers +
                ", totalCompletelySoldBatches=" + totalCompletelySoldBatches +
                ", totalBatchesForSell=" + totalBatchesForSell +
                ", consignees=" + consignees +
                ", assistants=" + assistants +
                ", commonInfo=" + commonInfo +
                '}';
    }
}

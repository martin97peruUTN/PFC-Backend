package pfc.consignacionhacienda.reports.dto;

import java.util.List;

public class Report {

    private GeneralInfo generalInfo;
    private List<CommonInfo> categoryList;

    public GeneralInfo getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(GeneralInfo generalInfo) {
        this.generalInfo = generalInfo;
    }

    public List<CommonInfo> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CommonInfo> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public String toString() {
        return "Report{" +
                "generalInfo=" + generalInfo +
                ", categoryList=" + categoryList +
                '}';
    }
}

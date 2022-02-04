package pfc.consignacionhacienda.reports.dto;

public class Consignee {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Consignee{" +
                "name='" + name + '\'' +
                '}';
    }
}

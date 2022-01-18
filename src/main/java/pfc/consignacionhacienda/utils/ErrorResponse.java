package pfc.consignacionhacienda.utils;

public class ErrorResponse {
    private String errorMsg;

    public ErrorResponse(String msg){
        errorMsg = msg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

package pfc.consignacionhacienda.utils;

public class JwtToken {
    private String token;
    public JwtToken(String access_token) {
        token = access_token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

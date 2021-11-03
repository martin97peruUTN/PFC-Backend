package pfc.consignacionhacienda.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;

public class SecurityConstants {
    @LocalServerPort
    private static int portNumber;

    public static final String AUTH_LOGIN_URL = "/api/login";
    public static final String JWT_SECRET = "perussiniravellipfcquedebetenermasde512bitsentoncestengoqueescribirmuchascosasparapodercumplirconloquediceelRFC7518";
    public static final String TOKEN_TYPE = "jwt";
    public static final String TOKEN_ISSUER = "consignacion-haciendas-backend";
    public static final String TOKEN_AUDIENCE = "consignacion-haciendas-frontend";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";


}

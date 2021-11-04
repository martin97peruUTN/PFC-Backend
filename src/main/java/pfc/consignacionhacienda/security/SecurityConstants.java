package pfc.consignacionhacienda.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

public class SecurityConstants {
    @LocalServerPort
    private static int portNumber;

    public static final String AUTH_LOGIN_URL = "/api/login";
    public static final SecretKey JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    public static final String TOKEN_TYPE = "jwt";
    public static final String TOKEN_ISSUER = "consignacion-haciendas-backend";
    public static final String TOKEN_AUDIENCE = "consignacion-haciendas-frontend";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";


}

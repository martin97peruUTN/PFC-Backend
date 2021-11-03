package pfc.consignacionhacienda.security;

import antlr.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.rest.login.LogInRest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MyAuthorizationFilter extends BasicAuthenticationFilter{

    private static final Logger logger = LoggerFactory.getLogger(MyAuthorizationFilter.class);
    private String error = "";
    public MyAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.TOKEN_HEADER);
        if(!req.getRequestURI().equals("/api/login")) {
            if ((header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX))) {
                error = "No se encontro token de acceso";
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                map.put("error", "Acceso denegado. " + error);
                ObjectMapper objectMapper = new ObjectMapper();
                error = objectMapper.writeValueAsString(map);
                res.setContentType("application/json");
                res.setStatus(HttpStatus.FORBIDDEN.value());
                res.getWriter().println(error);
                res.getWriter().flush();
                chain.doFilter(req, res);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            if (authentication == null) {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                map.put("error", "Acceso denegado. " + error);
                ObjectMapper objectMapper = new ObjectMapper();
                error = objectMapper.writeValueAsString(map);
                res.setContentType("application/json");
                res.setStatus(HttpStatus.FORBIDDEN.value());
                res.getWriter().println(error);
                res.getWriter().flush();
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (token != null && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            // parse the token.
            try {
                byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
                Jws<Claims> parsedToken = Jwts.parserBuilder().
                                                setSigningKey(signingKey)
                                                .build()
                                                .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX,""));
                List<SimpleGrantedAuthority> authorities;
                if(parsedToken.getBody().get("rol")!=null){

                    authorities = ((List<?>) parsedToken.getBody().get("rol"))
                                                                .stream()
                                                                .map(authority -> new SimpleGrantedAuthority((String) ((LinkedHashMap)authority).get("authority")))
                                                                .collect(Collectors.toList());
                    logger.debug(authorities.toString());
                    User usuario = new User();
                    //Armamos la informacion del usuario que luego se utilizara en SecurityConfig para asegurar las rutas, por ejemplo, por roles.
                    if(parsedToken.getBody().getSubject() != null){
                        usuario.setName(parsedToken.getBody().getSubject());
                    }
                    if(parsedToken.getBody().get("username") != null){
                        usuario.setUsername(parsedToken.getBody().get("username").toString());
                    }
                    if(parsedToken.getBody().get("uid") != null){
                        usuario.setId(Integer.parseInt(parsedToken.getBody().get("uid").toString()));
                    }
                    logger.debug("Usuario obtenido desde JWT: " + usuario.toString());
                    return  new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                }
            } catch (ExpiredJwtException exception){
                logger.warn("Request to parse expired JWT: {} failed: {}", token, exception.getMessage());
                error = "JWT expirado: " + token + " failed: " + exception.getMessage();
            } catch (UnsupportedJwtException exception){
                logger.warn("Request to parse unsupported JWT: {} failed: {}", token, exception.getMessage());
                error = "JWT no soportado: " + token + " failed: " + exception.getMessage();
            } catch (MalformedJwtException exception){
                logger.warn("Request to parse malformed JWT: {} failed: {}", token, exception.getMessage());
                error = "JWT mal formado: " + token + " failed: " + exception.getMessage();
            } catch (SignatureException exception){
                logger.warn("JWT with invalid signature: {} failed: {}", token, exception.getMessage());
                error = "Firma de JWT invalida: " + token + " failed: " + exception.getMessage();
            } catch (IllegalArgumentException exception){
                logger.warn("Request to parse empty or null JWT: {} failed: {}", token, exception.getMessage());
                error = "JWT vacio o nulo: "+token+" failed: "+ exception.getMessage();
            } catch (Exception exception){
                logger.warn("Request to parse other error JWT: {} failed: {}", token, exception.getMessage());
                error = "Error inesperado: "+token+" failed: "+ exception.getMessage();
            }
        }
        return null;
    }
}

package pfc.consignacionhacienda.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;

public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(MyAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;
    private String error = "";
    public MyAuthenticationFilter(AuthenticationManager authMgr ){
        this.authenticationManager = authMgr;
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String username = null, password = null;
        //Si la peticion viene en el body
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = mapper.readValue(request.getInputStream(),User.class);
            try{
                username = user.getUsername();
                password = user.getPassword();
                logger.debug(user.toString());
                if(username == null || password == null){
                    throw  new NullPointerException();
                }
            }catch (NullPointerException e){
                try {
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    error = "Existen parámetros nulos.";
                    map.put("error", error);
                    response.getWriter().println(mapper.writeValueAsString(map));
                    response.getWriter().flush();
                } catch (IOException ex) {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            try {
                error = "Error al obtener parámetros desde la solicitud HTTP.";
                map.put("error", error);
                response.getWriter().println(mapper.writeValueAsString(map));
                response.getWriter().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        try {
            //Realizamos la autenticacion con MyAuthenticationProvider y capturamos posibles excepciones.
            return authenticationManager.authenticate(authenticationToken);
        } catch (InvalidCredentialsException exception) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            try {
                error = exception.getMessage();
                map.put("error", error);
                response.getWriter().println(mapper.writeValueAsString(map));
                response.getWriter().flush();
            } catch (IOException e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return null;
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) throws ServletException, IOException {

       //Si la autenticacion fue exitosa, armamos el JWT que revolvemos a la app cliente.
        Principal principal = (Principal) authentication.getPrincipal();
        User usuario = new User();
        usuario.setName(principal.getName());
        usuario.setLastname(principal.getLastname());
        usuario.setUsername(principal.getUsername());
        usuario.setId(principal.getId());

        ObjectMapper objectMapper = new ObjectMapper();

        String token = Jwts.builder()
                        .signWith(SecurityConstants.JWT_SECRET, SignatureAlgorithm.HS512)
                        .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                        .setIssuer(SecurityConstants.TOKEN_ISSUER)
                        .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                        .setSubject(usuario.getName() + ' ' + usuario.getLastname())
                        .claim("name", usuario.getName())
                        .claim("lastname", usuario.getLastname())
                        .claim("uid", usuario.getId())
                        .claim("username", usuario.getUsername())
                        .setExpiration(new Date(System.currentTimeMillis() + 86400000)) //un dia
                        .claim("rol", authentication.getAuthorities())
                        .compact();
        logger.debug(token);

        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("access_token", token);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(objectMapper.writeValueAsString(map));

        //Seguimos con los filtro, o terminamos en el REST.
        filterChain.doFilter(request, response);
    }
}

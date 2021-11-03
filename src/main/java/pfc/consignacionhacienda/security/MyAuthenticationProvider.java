package pfc.consignacionhacienda.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.rest.login.LogInRest;
import pfc.consignacionhacienda.services.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Configurable
public class MyAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(MyAuthenticationProvider.class);

    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //Este metodo se encarga de verificar que el usario existe en la base de datos y compara sus contraseñas.
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        Optional<User> userOpt = userService.findByUsername(username);

        if(userOpt.isEmpty()){
            throw new InvalidCredentialsException("Las credenciales son inválidas");
        }
        User user = userOpt.get();
        if(!user.getPassword().equals(password)){
            throw new InvalidCredentialsException("Las credenciales son inválidas");
        }

        List<GrantedAuthority> rol = new ArrayList<>();
        rol.add(new SimpleGrantedAuthority(user.getRol()));

        //Este token es usado internamente por spring.
        return new UsernamePasswordAuthenticationToken(new Principal(user), null, rol);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}

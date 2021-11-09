package pfc.consignacionhacienda.services.user;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.el.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import pfc.consignacionhacienda.dao.UserDAO;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.security.SecurityConstants;
import pfc.consignacionhacienda.utils.ChangePassword;
import pfc.consignacionhacienda.utils.JwtToken;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Authentication authentication;

    @Override
    public Optional<User> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public User findUserById(Integer id) throws UserNotFoundException {
        Optional<User> user = userDAO.findById(id);
        if(user.isPresent()){
            return  user.get();
        }
        throw new UserNotFoundException("El usuario con id: " + id + " no existe");
    }

    @Override
    public JwtToken updateUserById(Integer id, Map<Object, Object> fields) {
        Optional<User> userOpt = userDAO.findById(id);

        if(fields.containsKey("id")){
            if(!((Integer) fields.get("id")).equals(id)){
                throw new InvalidCredentialsException("No se puede modificar el id del usuario");
            }
        }
        if(userOpt.isPresent()){
            User user = userOpt.get();
            User finalUser = user;
            fields.forEach((key, value) -> {
                if(value != null) {
                    Field field = ReflectionUtils.findField(User.class, (String) key);
                    if(field != null) {
                        field.setAccessible(true);
                        ReflectionUtils.setField(field, finalUser, value);
                    }
                }
            });
            user = saveUser(finalUser);
            String token = Jwts.builder()
                    .signWith(SecurityConstants.JWT_SECRET, SignatureAlgorithm.HS512)
                    .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                    .setIssuer(SecurityConstants.TOKEN_ISSUER)
                    .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                    .setSubject(user.getName())
                    .claim("uid", user.getId())
                    .claim("username", user.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) //un dia
                    .claim("rol", authentication.getAuthorities())
                    .compact();

            LinkedHashMap<String,String> map = new LinkedHashMap<>();
            map.put("access_token", token);
            return new JwtToken(map.get("access_token"));
        }
        throw new UserNotFoundException("El usuario con id: "+ id +" no existe");
    }

    @Override
    public void changePasswordById(Integer id, ChangePassword changePassword) {
        Optional<User> userOpt = userDAO.findById(id);

        if(userOpt.isPresent()){
            User user = userOpt.get();
            String oldDBpassword = user.getPassword();
            String newPasswordEncoded = passwordEncoder.encode(changePassword.getNewPassword());
            if(!passwordEncoder.matches(changePassword.getOldPassword(), oldDBpassword)){
                throw new InvalidCredentialsException("La contraseña antigua ingresada es diferente a su contraseña actual.");
            }
            if(passwordEncoder.matches(changePassword.getNewPassword(), oldDBpassword)){
                throw new InvalidCredentialsException("Las contraseñas deben ser distintas");
            }
            user.setPassword(newPasswordEncoded);
            saveUser(user);
        }else{
            throw new UserNotFoundException("Usuario con id: " + id + " no encontrado.");
        }
    }

    public User saveUser(User user){
        return userDAO.save(user);
    }
}

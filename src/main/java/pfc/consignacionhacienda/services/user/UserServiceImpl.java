package pfc.consignacionhacienda.services.user;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import pfc.consignacionhacienda.dao.UserDAO;
import pfc.consignacionhacienda.dto.UserDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.user.DuplicateUsernameException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.security.SecurityConstants;
import pfc.consignacionhacienda.utils.ChangePassword;
import pfc.consignacionhacienda.utils.JwtToken;
import pfc.consignacionhacienda.utils.UserMapper;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

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
    public JwtToken updateUserProfileById(Integer id, Map<Object, Object> fields) throws DuplicateUsernameException {
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
                    .setSubject(user.getName() + ' ' + user.getLastname())
                    .claim("name", user.getName())
                    .claim("lastname", user.getLastname())
                    .claim("uid", user.getId())
                    .claim("username", user.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) //un dia
                    .claim("rol", SecurityContextHolder.getContext().getAuthentication()==null? List.of("Rol"):SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                    .compact();

            LinkedHashMap<String,String> map = new LinkedHashMap<>();
            map.put("access_token", token);
            return new JwtToken(map.get("access_token"));
        }
        throw new UserNotFoundException("El usuario con id: "+ id +" no existe");
    }

    @Override
    public void changePasswordById(Integer id, ChangePassword changePassword) throws DuplicateUsernameException {
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
            userDAO.save(user);
        }else{
            throw new UserNotFoundException("Usuario con id: " + id + " no encontrado.");
        }
    }

    public User saveUser(User user) throws DuplicateUsernameException {
        Optional<User> u = findByUsername(user.getUsername());
        //Si estoy modificando un usuario
        if(u.isPresent()){
            if(user.getId()!=null){
                if(user.getId().equals(u.get().getId())){
                    if(user.getPassword() != null && !passwordEncoder.matches(user.getPassword(), u.get().getPassword())){
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    return userDAO.save(user);
                }
                throw new DuplicateUsernameException("Ya existe un usuario con este username.");
            }
            throw new DuplicateUsernameException("Ya existe un usuario con este username.");
        }

        //Si estoy creando un usuario
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDAO.save(user);
    }

    @Override
    public User getCurrentUser() {
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getCurrentUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    @Override
    public User deleteUserById(Integer id) throws DuplicateUsernameException, UserNotFoundException {
        User u = findUserById(id);
        if(u.isDeleted() != null && u.isDeleted()){
            throw new UserNotFoundException("No existe usuario con id: " + id);
        }
        u.setDeleted(true);
        return saveUser(u);
    }

    @Override
    public User updateUserById(Integer id, UserDTO fields) throws DuplicateUsernameException, HttpForbidenException, InvalidCredentialsException {
        if(fields.getId() != null){
            if(!fields.getId().equals(id)){
                throw new InvalidCredentialsException("No se puede modificar el id del usuario");
            }
        }
        User user = findUserById(id);
        if(fields.getRol() != null && !fields.getRol().equals(user.getRol())){
            throw new HttpForbidenException("Los roles no pueden modificarse");
        }
        userMapper.updateUserFromDto(fields, user);
        return saveUser(user);
    }

    @Override
    public Page<User> findUsersNotDeleted(Integer page, Integer size) {
        return userDAO.findByDeletedNotNullOrDeletedFalse(PageRequest.of(page, size));
    }

    @Override
    public Page<User> findUsersNotDeletedByUsername(String username, Integer page, Integer size) {
        return userDAO.findByUsernameAndNotDeleted(username, PageRequest.of(page, size));
    }

    @Override
    public Page<User> findUsersNotDeletedByName(Integer page, Integer size, String name) {
        return userDAO.getUsersNotDeletedByName(name, PageRequest.of(page, size));
    }

    @Override
    public Page<User> findAllUsersByPage( Integer page, Integer size) {
        return userDAO.findAll(PageRequest.of(page, size));
    }

    @Override
    public List<User> findAllUsers() {
        return userDAO.findAll();
    }
}

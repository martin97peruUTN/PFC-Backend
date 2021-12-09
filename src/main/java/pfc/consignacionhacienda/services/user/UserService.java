package pfc.consignacionhacienda.services.user;

import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import pfc.consignacionhacienda.dto.UserDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.user.DuplicateUsernameException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.utils.ChangePassword;
import pfc.consignacionhacienda.utils.JwtToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    User findUserById(Integer id) throws UserNotFoundException;

    JwtToken updateUserProfileById(Integer id, Map<Object, Object> fields) throws DuplicateUsernameException, BadHttpRequest;

    void changePasswordById(Integer id, ChangePassword changePassword) throws DuplicateUsernameException, HttpForbidenException;

    User saveUser(User u) throws DuplicateUsernameException, BadHttpRequest;

    User getCurrentUser();

    Collection<? extends GrantedAuthority>  getCurrentUserAuthorities();

    User deleteUserById(Integer id) throws DuplicateUsernameException, UserNotFoundException, BadHttpRequest;

    User updateUserById(Integer id, UserDTO fields) throws DuplicateUsernameException, InvalidCredentialsException, BadHttpRequest, HttpForbidenException;

    Page<User> findUsersNotDeleted(Integer page, Integer limit);

    Page<User> findUsersNotDeletedByUsername(String username, Integer page, Integer size);

    Page<User> findUsersNotDeletedByName(Integer page, Integer size, String name);

    Page<User> findAllUsersByPage( Integer page, Integer size);

    List<User> findAllUsers();

    List<User> findUsersByNameExceptIdAndNotAdmin(String name, Integer userId);
}

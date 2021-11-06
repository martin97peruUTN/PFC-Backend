package pfc.consignacionhacienda.services.user;

import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.utils.ChangePassword;
import pfc.consignacionhacienda.utils.JwtToken;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    User findUserById(Integer id) throws UserNotFoundException;

    JwtToken updateUserById(Integer id, Map<Object, Object> fields);

    void changePasswordById(Integer id, ChangePassword changePassword);
}

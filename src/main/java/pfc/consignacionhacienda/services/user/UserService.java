package pfc.consignacionhacienda.services.user;

import pfc.consignacionhacienda.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
}

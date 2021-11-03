package pfc.consignacionhacienda.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.UserDAO;
import pfc.consignacionhacienda.model.User;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public Optional<User> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}

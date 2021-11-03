package pfc.consignacionhacienda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pfc.consignacionhacienda.model.User;

import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {
    public Optional<User> findByUsername(String username);
}

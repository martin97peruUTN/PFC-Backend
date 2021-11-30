package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.User;

import java.util.Optional;
@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
    public Optional<User> findByUsername(String username);

    Page<User> findByDeletedNotNullOrDeletedFalse(PageRequest of);

    @Query("Select u from User u where u.username like %:username% and (u.deleted is null or u.deleted = false)")
    Page<User> findByUsernameAndNotDeleted(String username, Pageable of);

    @Query("Select u from User u where (u.name like %:name% or u.lastname like %:name%) and (u.deleted is null or u.deleted = false)")
    Page<User> getUsersNotDeletedByName(String name, Pageable of);
}

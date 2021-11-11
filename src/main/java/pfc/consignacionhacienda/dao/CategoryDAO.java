package pfc.consignacionhacienda.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Category;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Integer> {
}

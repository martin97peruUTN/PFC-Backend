package pfc.consignacionhacienda.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pfc.consignacionhacienda.model.Category;
import pfc.consignacionhacienda.model.Locality;

import java.util.List;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Integer> {
    Page<Category> findByDeletedNotNullAndDeletedFalseOrderByName(Pageable of);
    List<Category> findByDeletedNotNullAndDeletedFalseOrderByName();

    @Query("Select c from Category c where (c.name like %:name%) and (c.deleted is null or c.deleted = false) order by c.name")
    Page<Category> findByName(Pageable of, String name);
}

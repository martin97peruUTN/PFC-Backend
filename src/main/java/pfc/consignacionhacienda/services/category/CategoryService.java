package pfc.consignacionhacienda.services.category;

import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.exceptions.InternalServerException;
import pfc.consignacionhacienda.exceptions.category.CategoryNotFoundException;
import pfc.consignacionhacienda.exceptions.locality.LocalityNotFoundException;
import pfc.consignacionhacienda.model.Category;
import pfc.consignacionhacienda.model.Locality;

import java.util.List;

public interface CategoryService {
    Category getCategoryById(Integer id) throws CategoryNotFoundException;
    Page<Category> getAllCategoriesByPages(Integer pageNumber, Integer limit);
    List<Category> getAllCategories();
    Category saveCategory(Category category) throws BadHttpRequest;
    Category updateCategoryById(Integer id, Category category) throws CategoryNotFoundException, BadHttpRequest;
    Category deleteCategoryById(Integer id) throws InternalServerException, CategoryNotFoundException;
}

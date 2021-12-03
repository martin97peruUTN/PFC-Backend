package pfc.consignacionhacienda.services.category;

import org.springframework.data.domain.Page;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.InternalServerException;
import pfc.consignacionhacienda.exceptions.category.CategoryNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.Category;

import java.util.List;

public interface CategoryService {
    Category getCategoryById(Integer id) throws CategoryNotFoundException;
    Page<Category> getAllCategoriesByPages(Integer pageNumber, Integer limit) throws InvalidCredentialsException;
    List<Category> getAllCategories();

    List<Category> getAllAvailablesCategories();
    Page<Category> getCategoriesByName(Integer pageNumber, Integer limit, String name);

    Category saveCategory(Category category) throws BadHttpRequest;
    Category updateCategoryById(Integer id, Category category) throws CategoryNotFoundException, BadHttpRequest;
    Category deleteCategoryById(Integer id) throws InternalServerException, CategoryNotFoundException;
}

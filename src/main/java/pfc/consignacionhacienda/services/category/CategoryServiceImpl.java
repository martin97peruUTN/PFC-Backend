package pfc.consignacionhacienda.services.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.CategoryDAO;
import pfc.consignacionhacienda.dao.LocalityDAO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.InternalServerException;
import pfc.consignacionhacienda.exceptions.category.CategoryNotFoundException;
import pfc.consignacionhacienda.exceptions.locality.LocalityNotFoundException;
import pfc.consignacionhacienda.model.Category;
import pfc.consignacionhacienda.model.Locality;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    CategoryDAO categoryDAO;
    @Override
    public Category getCategoryById(Integer id) throws CategoryNotFoundException {
        Optional<Category> categoryOpt = categoryDAO.findById(id);
        if(categoryOpt.isPresent()){
            return categoryOpt.get();
        }
        throw new CategoryNotFoundException("No existe localidad con id: " + id);
    }

    @Override
    public Page<Category> getAllCategoriesByPages(Integer pageNumber, Integer limit) {
        Page<Category> categories = categoryDAO.findAll(PageRequest.of(pageNumber,limit));
        return categories;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    @Override
    public Category saveCategory(Category category) throws BadHttpRequest {
        if(category.getName()==null){
            throw new BadHttpRequest("El parametro name no puede ser nulo");
        }
        return categoryDAO.save(category);
    }

    @Override
    public Category updateCategoryById(Integer id, Category category) throws CategoryNotFoundException, BadHttpRequest {
        if(category.getId() != null && !category.getId().equals(id)){
            throw new BadHttpRequest("El parametro {id} no coincide con el id de la localidad que se esta por modificar.");
        }
        Category c = getCategoryById(id);
        c.setName(category.getName());
        return saveCategory(c);
    }

    @Override
    public Category deleteCategoryById(Integer id) throws InternalServerException, CategoryNotFoundException {
        Category c = getCategoryById(id);
        categoryDAO.deleteById(id);
        return c;
    }
}

package pfc.consignacionhacienda.services.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.CategoryDAO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.category.CategoryNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.Category;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryDAO categoryDAO;

    @Override
    public Category getCategoryById(Integer id) throws CategoryNotFoundException {
        Optional<Category> categoryOpt = categoryDAO.findById(id);
        if(categoryOpt.isPresent()){
            return categoryOpt.get();
        }
        throw new CategoryNotFoundException("No existe categoria con id: " + id);
    }

    @Override
    public Page<Category> getAllCategoriesByPages(Integer pageNumber, Integer limit) throws InvalidCredentialsException{
        if(pageNumber < 0 || limit < 0 ){
            throw new InvalidCredentialsException("Parametros invalidos.");
        }
        logger.debug(pageNumber + "   "+limit);
        return categoryDAO.findByDeletedNotNullAndDeletedFalse(PageRequest.of(pageNumber,limit));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    @Override
    public List<Category> getAllAvailablesCategories() {
        return categoryDAO.findByDeletedNotNullAndDeletedFalse();
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
            throw new BadHttpRequest("El parametro {id} no coincide con el id de la categoria que se esta por modificar.");
        }
        Category c = getCategoryById(id);
        if(category.isDeleted()){
            throw new CategoryNotFoundException("No existe categoria con id: " + id);
        }
        c.setName(category.getName());
        return saveCategory(c);
    }

    @Override
    public Category deleteCategoryById(Integer id) throws CategoryNotFoundException {
        Category c = getCategoryById(id);
        if(c.isDeleted()){
            throw new CategoryNotFoundException("No existe categoria con id: " + id);
        }
        c.setDeleted(true);
        return categoryDAO.save(c);
    }
}

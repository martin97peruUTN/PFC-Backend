package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.InternalServerException;
import pfc.consignacionhacienda.exceptions.category.CategoryNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.Category;
import pfc.consignacionhacienda.services.category.CategoryService;

@RestController
@RequestMapping("/api/category")
public class CategoryRest {
    private static final Logger logger = LoggerFactory.getLogger(LocalityRest.class);

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<Category> getLocalityById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (CategoryNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<Page<Category>> getAllCategories(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, name = "name", defaultValue = "") String categorySearchName){
        try {
            if(categorySearchName.isBlank()){
                return ResponseEntity.ok(categoryService.getAllCategoriesByPages(page,limit));
            }else{
                return ResponseEntity.ok(categoryService.getCategoriesByName(page, limit, categorySearchName));
            }
        }catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping()
    public ResponseEntity<Category> createCategory(@RequestBody Category newCategory){
        try {
            return ResponseEntity.ok(categoryService.saveCategory(newCategory));
        } catch (BadHttpRequest e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody Category newCategory){
        try {
            return ResponseEntity.ok(categoryService.updateCategoryById(id, newCategory));
        }catch (CategoryNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadHttpRequest e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteLocalityById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(categoryService.deleteCategoryById(id));
        } catch (CategoryNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (InternalServerException e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

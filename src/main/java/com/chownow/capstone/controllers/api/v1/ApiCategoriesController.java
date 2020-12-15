package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.Category;
import com.chownow.capstone.repos.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class ApiCategoriesController {

    @Autowired
    private CategoryRepository categoryDao;

    /* GET MAPPINGS */

    // get all categories
    @GetMapping
    public @ResponseBody
    List<Category> getAllCategories(){
        return categoryDao.findAll();
    }

    // get category by id
    @GetMapping("/{id}")
    public @ResponseBody Category getCategory(@PathVariable(value="id") long categoryId) {
        Optional<Category> category = categoryDao.findById(categoryId);
        return category.orElse(null);
    }

    /* POST MAPPINGS FOR CRUD */

    // create category
    @PostMapping("/new")
    public @ResponseBody
    Category createCategory(@RequestBody Category category){
        return categoryDao.save(category);
    }

    // update category
    @PostMapping("/{id}/edit")
    public @ResponseBody Category updateCategory(@RequestBody Category requestCategory,@PathVariable (value = "id")long categoryId){
        Optional<Category> category = categoryDao.findById(categoryId);
        if(category.isPresent()) {
            Category dbCategory = category.get();
            return categoryDao.save(dbCategory);
        }
        return null;
    }

    // delete category
    @PostMapping("/{id}/delete")
    public ResponseEntity<Category> deleteCategory(@PathVariable ("id") long categoryId){
        Optional<Category> category = categoryDao.findById(categoryId);
        if(category.isPresent()) {
            Category dbCategory = category.get();
            categoryDao.delete(dbCategory);
        }
        return ResponseEntity.ok().build();
    }

}

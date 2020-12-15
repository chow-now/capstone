package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.Ingredient;
import com.chownow.capstone.repos.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/ingredients")
public class ApiIngredientsController {
    @Autowired
    private IngredientRepository ingredientDao;

    /* GET MAPPINGS */

    // get all ingredients
    @GetMapping
    public @ResponseBody
    List<Ingredient> getAllIngredients(){
        return ingredientDao.findAll();
    }

    // get ingredient by id
    @GetMapping("/{id}")
    public @ResponseBody Ingredient getIngredient(@PathVariable(value="id") long ingredientId) {
        Optional<Ingredient> ingredient = ingredientDao.findById(ingredientId);
        return ingredient.orElse(null);
    }

    /* POST MAPPINGS FOR CRUD */

    // create ingredient
    @PostMapping("/new")
    public @ResponseBody
    Ingredient createIngredient(@RequestBody Ingredient ingredient){
        return ingredientDao.save(ingredient);
    }

    // update ingredient
    @PostMapping("/{id}/edit")
    public @ResponseBody Ingredient updateIngredient(@RequestBody Ingredient requestIngredient,@PathVariable (value = "id")long ingredientId){
        Optional<Ingredient> ingredient = ingredientDao.findById(ingredientId);
        if(ingredient.isPresent()) {
            Ingredient dbIngredient = ingredient.get();
            return ingredientDao.save(dbIngredient);
        }
        return null;
    }

    // delete ingredient
    @PostMapping("/{id}/delete")
    public ResponseEntity<Ingredient> deleteIngredient(@PathVariable ("id") long ingredientId){
        Optional<Ingredient> ingredient = ingredientDao.findById(ingredientId);
        if(ingredient.isPresent()) {
            Ingredient dbIngredient = ingredient.get();
            ingredientDao.delete(dbIngredient);
        }
        return ResponseEntity.ok().build();
    }

}

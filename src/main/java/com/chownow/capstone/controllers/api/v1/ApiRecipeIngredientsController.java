package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.RecipeIngredient;
import com.chownow.capstone.repos.RecipeIngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipe-ingredients")
public class ApiRecipeIngredientsController {
    @Autowired
    private RecipeIngredientRepository recipeIngredientDao;

    /* GET MAPPINGS */

    // get all recipeIngredients
    @GetMapping
    public @ResponseBody
    List<RecipeIngredient> getAllRecipeIngredients(){
        return recipeIngredientDao.findAll();
    }

    // get recipeIngredient by id
    @GetMapping("/{id}")
    public @ResponseBody RecipeIngredient getRecipeIngredient(@PathVariable(value="id") long recipeIngredientId) {
        Optional<RecipeIngredient> recipeIngredient = recipeIngredientDao.findById(recipeIngredientId);
        return recipeIngredient.orElse(null);
    }

    /* POST MAPPINGS FOR CRUD */

    // create recipeIngredient
    @PostMapping("/new")
    public @ResponseBody
    RecipeIngredient createRecipeIngredient(@RequestBody RecipeIngredient recipeIngredient){
        return recipeIngredientDao.save(recipeIngredient);
    }

    // update recipeIngredient
    @PostMapping("/{id}/edit")
    public @ResponseBody RecipeIngredient updateRecipeIngredient(@RequestBody RecipeIngredient requestRecipeIngredient,@PathVariable (value = "id")long recipeIngredientId){
        Optional<RecipeIngredient> recipeIngredient = recipeIngredientDao.findById(recipeIngredientId);
        if(recipeIngredient.isPresent()) {
            RecipeIngredient dbRecipeIngredient = recipeIngredient.get();
            return recipeIngredientDao.save(dbRecipeIngredient);
        }
        return null;
    }

    // delete recipeIngredient
    @PostMapping("/{id}/delete")
    public ResponseEntity<RecipeIngredient> deleteRecipeIngredient(@PathVariable ("id") long recipeIngredientId){
        Optional<RecipeIngredient> recipeIngredient = recipeIngredientDao.findById(recipeIngredientId);
        if(recipeIngredient.isPresent()) {
            RecipeIngredient dbRecipeIngredient = recipeIngredient.get();
            recipeIngredientDao.delete(dbRecipeIngredient);
        }
        return ResponseEntity.ok().build();
    }
}

package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.Recipe;

import com.chownow.capstone.repos.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
public class ApiRecipeController {

    @Autowired
    private RecipeRepository recipeDao;

    /* GET MAPPINGS */

    // get all users
    @GetMapping
    public @ResponseBody
    List<Recipe> getAllRecipes(){
        return recipeDao.findAll();
    }

    // get recipe by id
    @GetMapping("/{id}")
    public @ResponseBody Recipe getRecipe(@PathVariable (value="id") long recipeId) {
        Optional<Recipe> recipe = recipeDao.findById(recipeId);
        return recipe.orElse(null);
    }

    /* POST MAPPINGS FOR CRUD */

    // create recipe
    @PostMapping("/new")
    public Recipe createRecipe(@RequestBody Recipe recipe){
        return recipeDao.save(recipe);
    }

    // update recipe
    @PostMapping("/{id}/edit")
    public Recipe updateRecipe(@RequestBody Recipe requestRecipe,@PathVariable (value = "id")long recipeId){
        Optional<Recipe> recipe = recipeDao.findById(recipeId);
        if(recipe.isPresent()) {
            Recipe dbRecipe = recipe.get();
            return recipeDao.save(dbRecipe);
        }
        return null;
    }

    // delete recipe
    @PostMapping("/{id}/delete")
    public ResponseEntity<Recipe> deleteRecipe(@PathVariable ("id") long recipeId){
        Optional<Recipe> recipe = recipeDao.findById(recipeId);
        if(recipe.isPresent()) {
            Recipe dbRecipe = recipe.get();
            recipeDao.delete(dbRecipe);
        }
        return ResponseEntity.ok().build();
    }
    
}

package com.chownow.capstone.controllers;

import com.chownow.capstone.models.Ingredient;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.RecipeIngredient;
import com.chownow.capstone.models.SpoonApiDto;
import com.chownow.capstone.repos.RecipeRepository;
import com.chownow.capstone.services.RecipeService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class SearchExampleController {
    @Value("${spoonacular.api.key}")
    private String spoonApi;

    @Autowired
    private RecipeRepository recipeDao;

    @Autowired
    private RecipeService recipeService;

    // With Javascript
    @GetMapping("/search")
    public String getRecipes(@RequestParam(required = false) String term,Model viewModel) throws InterruptedException, ParseException, IOException {
        viewModel.addAttribute("term", term);
        viewModel.addAttribute("spoonApi", spoonApi);
        viewModel.addAttribute("recipe", new SpoonApiDto());

        return "recipes/search";
    }

    @PostMapping("/search")
    public String saveRecipes(Model viewModel,
                              @ModelAttribute SpoonApiDto recipe,
                              @ModelAttribute Recipe recipe1){
        // Find if recipe already exists in DB
        Recipe existingRecipe = recipeDao.findFirstBySpoonApiId(recipe1.getSpoonApiId());

        // If not found, save to DB
        if (existingRecipe == null) {
            recipeService.saveRecipe(recipe);
        }
        return "recipes/search";
    }
}

package com.chownow.capstone.controllers;

import com.chownow.capstone.models.SpoonApiDto;
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
                              @ModelAttribute SpoonApiDto recipe){

        recipeService.saveRecipe(recipe);

        return "recipes/search";
    }
}

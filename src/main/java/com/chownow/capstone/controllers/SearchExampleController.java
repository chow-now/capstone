package com.chownow.capstone.controllers;

import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.repos.RecipeRepository;
//import com.chownow.capstone.services.SpoonApi;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;


@Controller
public class SearchExampleController {
    @Value("${spoonacular.api.key}")
    private String spoonApi;
    // With Java
//    @GetMapping("/search")
//    public String getRecipes(@RequestParam(required = false, name="term") String term, Model viewModel) throws InterruptedException, ParseException, IOException {
////        viewModel.addAttribute("term", SpoonApi.getRecipes("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=" + term + "&number=10&offset=0"));
////        empty[]
//        List<Recipe> listReturned = SpoonApi.getRecipes("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=" + term + "&number=10&offset=0");
//        viewModel.addAttribute("searchResults", listReturned);
//        return "recipes/search";
//    }

    // With Javascript
    // test to search recipe by recipe name
    @GetMapping("/search")
    public String getRecipes(@RequestParam(required = false) String term,Model viewModel) throws InterruptedException, ParseException, IOException {
        viewModel.addAttribute("term", term);
        viewModel.addAttribute("spoonApi", spoonApi);
        // test commit

        return "recipes/search";
    }
}

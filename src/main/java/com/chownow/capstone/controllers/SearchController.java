package com.chownow.capstone.controllers;

import com.chownow.capstone.repos.RecipeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Controller
public class SearchController {
    private final RecipeRepository recipeRepository;

    public SearchController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }
    @GetMapping("/recipes/search")
    public String getRecipes(@RequestParam(required=false) String term, Model viewModel) throws Exception {
        get("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=" + term + "&number=100&offset=0");
        viewModel.addAttribute("searchResults", recipeRepository.findAllByTitle(term));
        return "recipes/results";
    }

    public void get(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}

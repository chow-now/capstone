package com.chownow.capstone.controllers;
import com.chownow.capstone.models.Category;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.repos.RecipeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ApiTest {

    private final RecipeRepository recipeDao;

    public ApiTest(RecipeRepository recipeDao) {
        this.recipeDao = recipeDao;
    }

    public String testing() {
        Config config = new Config();
        HttpRequest request = HttpRequest.newBuilder()
                // GET Search endpoint
                .uri(URI.create("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=pasta&number=20&offset=0&type=main%20course"))
                .header("x-rapidapi-key", config.getKey())
                .header("x-rapidapi-host", config.getHost())
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        {
            try {
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response.body();
    }


    @GetMapping("/recipe/search")
    public String search(Model viewModel) {
    List <Recipe> recipes = formatSpoonacularJSON(testing()); // format json -> List<Object>
        recipes = recipeDao.findAll();
        viewModel.addAttribute("searchResults", recipes);
        return "recipes/results";
    }

    // Formatting process
    private List <Recipe> formatSpoonacularJSON(String json){
        // TODO : parse JSON string
//        JSONObject recipeList = new JSONObject(json);
//        String recipeName = recipeList.getJSONObject("results").getString("title");
//        System.out.println(recipeName);
        try {
            List <Recipe> recipes = new ObjectMapper().readValue(json, new TypeReference<List<Recipe>>() {});
            return recipes;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

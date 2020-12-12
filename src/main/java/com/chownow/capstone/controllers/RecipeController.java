package com.chownow.capstone.controllers;

import com.chownow.capstone.models.Category;
import com.chownow.capstone.models.Image;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.RecipeIngredient;
import com.chownow.capstone.repos.RecipeRepository;
import com.chownow.capstone.repos.UserRepository;
import org.hibernate.cache.spi.CacheTransactionSynchronization;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class RecipeController {

    private final RecipeRepository recipeDao;
    private final UserRepository userDoa;
    //    add email service?

    public RecipeController(RecipeRepository recipeDao, UserRepository userDoa) {
        this.recipeDao = recipeDao;
        this.userDoa = userDoa;
    }

    @GetMapping("/recipes")
    public String index(Model model) {
        model.addAttribute("recipes", recipeDao.findAll());
        return "recipes/index";
    }

    @GetMapping("/recipes/{id}")
    public String showRecipe(@PathVariable long id, Model model) {
        model.addAttribute("recipe", recipeDao.getOne(id));
        Recipe recipe = recipeDao.getOne(id);

        String firstName = recipe.getChef().getFirstName();
        String lastName = recipe.getChef().getLastName();
        String chef = firstName + " " + lastName;
        model.addAttribute("chef", chef);

        String title = recipe.getTitle();
        model.addAttribute("title", title);

        String directions = recipe.getDirections();
        model.addAttribute("directions", directions);

        String difficulty = recipe.getDifficulty();
        model.addAttribute("difficulty", difficulty);

        int cookTime = recipe.getCookTime();
        model.addAttribute("cookTime", cookTime);

        int prepTime = recipe.getPrepTime();
        model.addAttribute("prepTime", prepTime);

        int servings = recipe.getServings();
        model.addAttribute("servings", servings);

        List<Image> images = recipe.getImages();
        model.addAttribute("images", images);

        List<RecipeIngredient> ingredients = recipe.getIngredients();
        model.addAttribute("ingredients", ingredients);

        List<Category> categories = recipe.getCategories();
        model.addAttribute("categories", categories);

        return "recipes/show";

    }


}

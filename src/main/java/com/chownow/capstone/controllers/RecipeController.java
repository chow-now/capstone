package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.RecipeRepository;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
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

//        List<Image> images = recipe.getImages();
//        model.addAttribute("images", images);
//
//        List<RecipeIngredient> ingredients = recipe.getIngredients();
//        model.addAttribute("ingredients", ingredients);
//
//        List<Category> categories = recipe.getCategories();
//        model.addAttribute("categories", categories);

        return "recipes/show";

    }

    @GetMapping("/recipes/new")
    public String showCreateRecipe(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "recipes/new";
    }
    /* new recipe with error handling */
    @PostMapping("/recipe/new")
    public String submitPost(
            @Valid @ModelAttribute Recipe recipeToBeSaved,
            Errors validation,
            Model model
    ) {
        if(validation.hasErrors()){
            model.addAttribute("errors",validation);
            model.addAttribute("recipe",recipeToBeSaved);
            return "recipes/new";
        }

//        User userDb = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        recipeToBeSaved.setChef(userDb);

        recipeToBeSaved.setChef(userDoa.getOne(1L));
        Recipe dbRecipe = recipeDao.save(recipeToBeSaved);

        return "redirect:/recipes/" + dbRecipe.getId();
    }

    @PostMapping("/recipes/{id}/delete")
    public String deleteRecipe(@PathVariable long id) {
        recipeDao.deleteById(id);
        return "redirect:/recipes";
    }

    @GetMapping("/recipes/{id}/edit")
    public String showEditRecipe(@PathVariable long id, Model model) {
        model.addAttribute("recipe", recipeDao.getOne(id));
        return "recipes/edit";
    }

    @PostMapping("/recipes/{id}/edit")
    public String editRecipe(Recipe recipeToBeSaved) {
//        User userDb = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        recipeToBeSaved.setChef(userDb);
        recipeDao.save(recipeToBeSaved);
        return "redirect:/recipes";
    }
}

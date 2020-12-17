package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.RecipeRepository;
import com.chownow.capstone.repos.UserRepository;
//import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class RecipeController {

    @Autowired
    private RecipeRepository recipeDao;
    @Autowired
    private UserRepository userDoa;
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
        String chef = firstName;
        model.addAttribute("chef", chef);
        List<Image> images = recipe.getImages();
        model.addAttribute("images", images);
        List<RecipeIngredient> ingredients = recipe.getRecipeIngredients();
        model.addAttribute("ingredients", ingredients);
        Set<Category> categories = recipe.getCategories();
        model.addAttribute("categories", categories);
        return "recipes/show";

    }

    @GetMapping("/recipes/new")
    public String showCreateRecipe(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "recipes/new";
    }
    /* new recipe with error handling */
    @PostMapping("/recipes/new")
    public String submitRecipe(
            @Valid @ModelAttribute Recipe recipeToBeSaved,
            Errors validation,
            Model model
    ) {
        System.out.println("hello");
        if(validation.hasErrors()){
            model.addAttribute("errors",validation);
            model.addAttribute("recipe",recipeToBeSaved);
            return "recipes/new";
        }
//        User userDb = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        recipeToBeSaved.setChef(userDb);

        recipeToBeSaved.setChef(userDoa.getOne(1L));
        System.out.println(recipeToBeSaved.getChef());
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
    public String editRecipe(@ModelAttribute Recipe recipeToBeSaved) {
//        User userDb = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recipeToBeSaved.setChef(userDoa.getOne(1L));

        recipeDao.save(recipeToBeSaved);
        return "redirect:/recipes";
    }
}

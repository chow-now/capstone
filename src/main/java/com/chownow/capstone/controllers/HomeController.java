package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.*;
import com.chownow.capstone.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private FollowRepository followDao;

    @Autowired
    private RecipeIngredientRepository recipeIngredientDao;

    @Autowired
    private RecipeRepository recipeDao;

    @Autowired
    private PantryIngredientRepository pantryIngredientDao;

    @Autowired
    private PantryRepository pantryDao;

    @Autowired
    private RecipeService recipeServ;

    @GetMapping("/")
    public String showHomepage(Model model) {
        model.addAttribute("topRecipes",recipeServ.getTopFavorites());
        return "/index";
    }
}


package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.*;
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

    @GetMapping("/")
    public String showHomepage(Model model) {
        return "/coming-soon";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        return "/aboutus";
    }

    @GetMapping("/home")
    public String showUsers(Model model) {
        model.addAttribute("recipes", recipeDao.findAll());
        model.addAttribute("users", userDao.findAll());

        return "/index";
    }
}


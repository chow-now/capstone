package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.*;
import com.chownow.capstone.services.RecipeService;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {
    @Autowired
    private UserService userServ;

    @Autowired
    private RecipeService recipeServ;

    @GetMapping("/")
    public String showHomepage(Model model) {
        if(userServ.isLoggedIn()){
            return "redirect:/dashboard";
        }
        model.addAttribute("topRecipes",recipeServ.getTopFavorites());
        return "index";
    }

    @GetMapping("/about")
    public String showAbout(){
        return "about";
    }
}


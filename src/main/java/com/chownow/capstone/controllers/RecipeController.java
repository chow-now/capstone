package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;

import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.*;

@Controller
public class RecipeController {

    @Autowired
    private RecipeRepository recipeDao;

    @Autowired
    private UserRepository userDoa;

    @Autowired
    private IngredientRepository ingredientDao;

    @Autowired
    PantryIngredientRepository pantryIngDao;

    @Autowired
    private RecipeIngredientRepository recipeIngDao;

    @Autowired
    private CategoryRepository categoryDao;

    @Autowired
    private UserService userServ;

    @Autowired
    private UserRepository userDao;

    @GetMapping("/recipes")
    public String index(Model model) {
        model.addAttribute("recipes", recipeDao.findAllByIsPublishedTrue());
        return "recipes/index";
    }

    @GetMapping("/recipes/{id}")
    public String showRecipe(@PathVariable long id, Model model) {
        Recipe recipe = recipeDao.getFirstById(id);
        User currentUser = userServ.loggedInUser();
        if(recipe == null){
            return "redirect:/recipes";
        }
        if(!recipe.isPublished()){
            System.out.println("Not Published.... redirecting");
            return "redirect:/recipes/"+recipe.getId()+"/edit";
        }
        Set<User> favoritedBy = recipe.getFavoritedBy();
        boolean canFavorite = true;
        if(favoritedBy.contains(currentUser)){
            System.out.println("Already favorited");
            canFavorite = false;
        }
        model.addAttribute("recipe", recipe);
        model.addAttribute("isOwner",userServ.isOwner(recipe.getChef()));
        model.addAttribute("canFavorite",canFavorite);
        return "recipes/show";

    }

    @GetMapping("/recipes/new")
    public String showCreateRecipe(Model model) {
        User currentUser = userServ.loggedInUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("isOwner",userServ.isOwner(currentUser));
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("categories", categoryDao.findAll());
        return "recipes/new";
    }

    /* new recipe with error handling */
    @PostMapping("/recipes/new")
    public String submitRecipe(
            @Valid @ModelAttribute Recipe recipeToBeSaved,
            Errors validation,
            Model model
    ) {
        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("recipe", recipeToBeSaved);
            return "recipes/new";
        }

        User currentUser = userServ.loggedInUser();
        recipeToBeSaved.setChef(userDoa.getOne(currentUser.getId()));
        recipeDao.save(recipeToBeSaved);

        model.addAttribute("recipe",recipeToBeSaved);
        model.addAttribute("isOwner",userServ.isOwner(currentUser));
        model.addAttribute("categories", categoryDao.findAll());

        return "recipes/new";
    }

    // CREATE A NEW RECIPE INGREDIENT
    @RequestMapping(value = "/recipe/{recipeId}/ingredient/new", method = RequestMethod.POST, headers = "Content-Type" +
            "=application/json")
    public @ResponseBody
    String postRecipeIngredient(@RequestBody AjaxRecipeIngredientRequest recipeIngredient,
                                @PathVariable long recipeId) {
        Recipe currentRecipe = recipeDao.getOne(recipeId);
        Ingredient dbIngredient = null;
        boolean isNotInDb = true;
        for (Ingredient i : ingredientDao.findAllByNameLike(recipeIngredient.getName())) {
            if (recipeIngredient.getName().equalsIgnoreCase(i.getName())) {
                dbIngredient = i;
                isNotInDb = false;
                break;
            }
        }
        if (isNotInDb) {
            dbIngredient = ingredientDao.save(new Ingredient(recipeIngredient.getName().toLowerCase()));
        }
        RecipeIngredient newRecipeIng = new RecipeIngredient(
                recipeIngredient.getAmount(),
                recipeIngredient.getUnit(),
                currentRecipe,
                dbIngredient
        );
        recipeIngDao.save(newRecipeIng);
        return "im done";
    }

    // EDIT RECIPE INGREDIENT
    @RequestMapping(value = "/recipe/ingredient/edit", method = RequestMethod.POST, headers = "Content-Type" +
            "=application/json")
    public @ResponseBody
    String editRecipeIngredient(@RequestBody AjaxRecipeIngredientRequest recipeIngredient) {
        RecipeIngredient pi = recipeIngDao.getOne(recipeIngredient.getId());
        pi.setAmount(recipeIngredient.getAmount());
        pi.setUnit(recipeIngredient.getUnit());
        recipeIngDao.save(pi);
        return "update complete";
    }

    @PostMapping("/recipes/{id}/delete")
    public String deleteRecipe(@PathVariable long id) {
        recipeDao.deleteById(id);
        return "redirect:/recipes";
    }

    @GetMapping("/recipes/{id}/edit")
    public String showEditRecipe(@PathVariable long id, Model model) {
        Recipe recipe = recipeDao.getOne(id);
        User user = userDao.getOne(recipe.getChef().getId());
        // restrict access to owner redirects others
        if(!userServ.isOwner(user)){
            return "redirect:/recipes";
        }
        model.addAttribute("recipe", recipe);
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("isOwner",userServ.isOwner(user));
        return "recipes/edit";
    }

    @PostMapping("/recipes/{id}/edit")
    public String editRecipe(
            @ModelAttribute Recipe recipeToBeSaved,
            Errors validation,
            Model model) {

        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("recipe", recipeToBeSaved);
            return "recipes/" + recipeToBeSaved.getId() + "/edit";
        }
        User currentUser = userServ.loggedInUser();
        recipeToBeSaved.setChef(userDoa.getOne(currentUser.getId()));
        Recipe dbRecipe = recipeDao.save(recipeToBeSaved);
        return "redirect:/recipes/" + dbRecipe.getId() +"/edit";
    }
    // CREATE NEW RECIPE CATEGORIES
//    @RequestMapping(
//            value = "/recipes/{recipeId}/categories/new",
//            method = RequestMethod.POST,
//            headers = "Content-Type=application/json")
//    public void createRecipeCategories(
//            @RequestBody Map<String, Object> payload,
//            @PathVariable long recipeId) {
//
//        System.out.println(payload);
//
//    }

    @PostMapping("/recipes/{id}/categories/new")
    @ResponseBody
    public String addRecipeCategories(@PathVariable long id) {



        return "done";
    }


   @PostMapping("/recipes/{id}/favorite")
    public String toggleFavorite(@PathVariable long id){
       User currentUser = userServ.loggedInUser();
       Recipe recipe = recipeDao.getOne(id);
       Set<User> favoritedBy = recipe.getFavoritedBy();
       if(favoritedBy.contains(currentUser)){
           favoritedBy.remove(currentUser);
       }else{
           favoritedBy.add(currentUser);
       }
       recipe.setFavoritedBy(favoritedBy);
       userDao.save(currentUser);
       return "redirect:/recipes/" + id;
   }
}

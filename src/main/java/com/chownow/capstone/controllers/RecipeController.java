package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;


import com.chownow.capstone.services.RecipeService;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.chownow.capstone.services.AmazonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping
public class RecipeController {

    @Autowired
    private RecipeRepository recipeDao;

    @Autowired
    private UserRepository userDao;

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
    private ImageRepository imageDao;

    @Autowired
    private AmazonService s3;


    @Value("${spoonacular.api.key}")
    private String spoonApi;

    @Autowired
    private RecipeService recipeService;

//    @GetMapping("/recipes")
//    public String index(Model model) {
//        model.addAttribute("recipes", recipeDao.findAllByIsPublishedTrue());
//        return "recipes/index";
//    }

    @GetMapping("/recipes")
    public String getRecipes(@RequestParam(required = false) String term,Model viewModel) throws InterruptedException, ParseException, IOException {
        viewModel.addAttribute("term", term);
        viewModel.addAttribute("spoonApi", spoonApi);
        viewModel.addAttribute("recipe", new SpoonApiDto());
        //viewModel.addAttribute("recipes", recipeDao.findAllByIsPublishedTrue());
        return "recipes/index";
    }

    /** List results **/
    @PostMapping("/recipes")
    public String saveRecipes(@ModelAttribute SpoonApiDto recipe,
                              Model viewModel){

        String message = recipeService.saveRecipe(recipe);
        // For testing/indicator
        viewModel.addAttribute("message", message);
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
    public String showCreateRecipe(
            Model model)
    {
        Recipe recipe = new Recipe();
//        recipe.setRecipeIngredients(recipeIngDao.findAllByRecipe(recipe));
//        recipe.setImages(imageDao.findAllByRecipe(recipe));

        User currentUser = userServ.loggedInUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("isOwner",userServ.isOwner(currentUser));
        model.addAttribute("recipe", recipe);
        model.addAttribute("categories", categoryDao.findAll());

        return "recipes/new";
    }

    /* new recipe with error handling */
    @PostMapping("/recipes/new")
    public String submitRecipe(
            @Valid @ModelAttribute Recipe recipeToBeSaved,
            Errors validation,
            Model model) {

        // RECIPE MODEL VALIDATIONS
        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("recipe", recipeToBeSaved);
            return "recipes/new";
        }
        // ADD FIELDS TO EXISTING RECIPE
//        recipe.setRecipeIngredients(recipeToBeSaved.getRecipeIngredients());
//        recipe.setImages(recipeToBeSaved.getImages());


        User currentUser = userServ.loggedInUser();
        recipeToBeSaved.setChef(userDao.getOne(currentUser.getId()));
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
        RecipeIngredient ri = recipeIngDao.getOne(recipeIngredient.getId());
        ri.setAmount(recipeIngredient.getAmount());
        ri.setUnit(recipeIngredient.getUnit());
        recipeIngDao.save(ri);
        return "update complete";
    }

    @PostMapping("/recipes/{id}/delete")
    public String deleteRecipe(@PathVariable long id) {
        recipeDao.deleteById(id);
        return "redirect:/recipes";
    }

    @GetMapping("/recipes/{id}/edit")
    public String showEditRecipe(
            @PathVariable long id,
            Model model)
    {
        Recipe recipe = recipeDao.getOne(id);
        User userId = userDao.getOne(recipe.getChef().getId());
        // restrict access to owner redirects others
        if(!userServ.isOwner(userId)){
            return "redirect:/recipes";
        }
//        recipe.setRecipeIngredients(recipeIngDao.findAllByRecipe(recipe));
//        recipe.setImages(imageDao.findAllByRecipe(recipe));
        model.addAttribute("recipe", recipe);
        User currentUser = userServ.loggedInUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("isOwner",userServ.isOwner(userId));
        return "recipes/edit";
    }

    @PostMapping("/recipes/{id}/edit")
    public String editRecipe(
            @PathVariable long id,
            @ModelAttribute Recipe recipeToBeSaved,
            Errors validation,
            Model model) {

        Recipe recipe = recipeDao.getOne(id);
        // RECIPE MODEL VALIDATIONS
        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("recipe", recipeToBeSaved);
            return "recipes/" + recipeToBeSaved.getId() + "/edit";
        }
        // ADD FIELDS TO EXISTING RECIPE
        recipe.setRecipeIngredients(recipeToBeSaved.getRecipeIngredients());
        recipe.setImages(recipeToBeSaved.getImages());

        recipe = recipeDao.save(recipe);
        model.addAttribute("recipe", recipe);

//        User currentUser = userServ.loggedInUser();
//        recipeToBeSaved.setChef(userDao.getOne(currentUser.getId()));
//        Recipe dbRecipe = recipeDao.save(recipeToBeSaved);
        return "redirect:/recipes/" + recipe.getId() +"/edit";
    }

    @RequestMapping(value = "/recipes/{id}/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadRecipeImage(@PathVariable long id, @RequestParam("file") MultipartFile multipartFile,
                                               Model model){
        Recipe recipe = recipeDao.getOne(id);
        s3.uploadRecipeImage(multipartFile, recipe);
//        model.addAttribute("recipe",recipe);
        return "uploaded";
    }

    @PostMapping("/recipes/image/{id}/delete")
    @ResponseBody
    public void deleteFile(@PathVariable long id, @RequestParam String imageUrl){
        imageDao.deleteById(id);
        s3.deleteFile(imageUrl);
    }

    @PostMapping("/recipes/{id}/publish")
    public String publishRecipe(@PathVariable long id){
        Recipe recipe = recipeDao.getOne(id);
        recipe.setPublished(true);

        return "users/profile";
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

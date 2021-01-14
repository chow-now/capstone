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

    @Autowired
    private FavoriteRepository favDao;

    @GetMapping("/recipes")
    public String getRecipes(@RequestParam(required = false) String term, Model viewModel) throws InterruptedException, ParseException, IOException {
        viewModel.addAttribute("term", term);
        viewModel.addAttribute("spoonApi", spoonApi);


        //viewModel.addAttribute("allrecipe", recipeDao.findAllByIsPublishedTrue());

        if (term == null) {
            List<Recipe> allrecipe = recipeDao.findAll();
            viewModel.addAttribute("allrecipe", allrecipe);
        }
         else {
            List<Recipe> recipes = recipeDao.findAll();
            List<Recipe> searchedRecipes = new ArrayList<>();

            for (Recipe recipe : recipes) {

                if (term != null) {
                    if (recipe.getTitle().toLowerCase().contains(term.toLowerCase())) {
                        searchedRecipes.add(recipe);
                        continue;
                    }
                    String[] searchArray = term.replaceAll(", ", ",").split(",");
                    ArrayList<String> ingredientArray = new ArrayList<>();

                    recipe.getRecipeIngredients().forEach(ingredient -> {
                        ingredientArray.add(String.valueOf(ingredient.getIngredient()));
                    });

                    //separate ingredient string into an array
                    boolean searchFlag = true;
                    for (String s : searchArray) {
                        if (!ingredientArray.toString().toLowerCase().contains(s.toLowerCase())) {
                            searchFlag = false;
                            break;
                        }
                    }
                    if (searchFlag) {
                        searchedRecipes.add(recipe);
                    }
                }
            }
           // viewModel.addAttribute("recipes", searchedRecipes);
            viewModel.addAttribute("Newrecipe", searchedRecipes);
        }
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
        Favorite favorite = favDao.findByUserAndAndRecipe(currentUser,recipe);
        boolean canFavorite = true;
        if(favorite != null){
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
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("user", userServ.loggedInUser());
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
            model.addAttribute("user", userServ.loggedInUser());
            model.addAttribute("categories", categoryDao.findAll());
            return "recipes/new";
        }
        // Save the Recipe to the current User
        User currentUser = userServ.loggedInUser();
        recipeToBeSaved.setChef(currentUser);
        Recipe dbRecipe = recipeDao.save(recipeToBeSaved);

        return "redirect:/recipes/"+dbRecipe.getId()+"/edit";
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
        recipeService.deleteRecipe(recipeDao.getOne(id));
        return "redirect:/dashboard";
    }

    @GetMapping("/recipes/{id}/edit")
    public String showEditRecipe(
            @PathVariable long id,
            Model model)
    {
        User currentUser = userServ.loggedInUser();
        Recipe recipe = recipeDao.getOne(id);
        // restrict access to owner redirects others
        if(!userServ.isOwner(recipe.getChef())){
            return "redirect:/recipes";
        }
//        recipe.setRecipeIngredients(recipeIngDao.findAllByRecipe(recipe));
//        recipe.setImages(imageDao.findAllByRecipe(recipe));
        model.addAttribute("recipe", recipe);
        model.addAttribute("user", currentUser);
        model.addAttribute("categories", categoryDao.findAll());
        model.addAttribute("isOwner",userServ.isOwner(recipe.getChef()));
        return "recipes/edit";
    }

    @PostMapping("/recipes/{id}/edit")
    public String editRecipe(
            @PathVariable long id,
            @Valid @ModelAttribute Recipe recipeToBeSaved,
            Errors validation,
            Model model) {
        // RECIPE MODEL VALIDATIONS
        Recipe recipe = recipeDao.getOne(id);
        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("recipe", recipeToBeSaved);
            model.addAttribute("user", userServ.loggedInUser());
            model.addAttribute("categories", categoryDao.findAll());
            model.addAttribute("isOwner",userServ.isOwner(recipe.getChef()));
            return "recipes/edit";
        }



        // ADD FIELDS TO EXISTING RECIPE

//        model fields

        System.out.println(recipeToBeSaved.getTitle());
        recipe.setTitle(recipeToBeSaved.getTitle());
        System.out.println(recipeToBeSaved.getDescription());
        recipe.setDescription(recipeToBeSaved.getDescription());
        System.out.println("ck "+recipeToBeSaved.getCookTime());
        recipe.setCookTime(recipeToBeSaved.getCookTime());
        System.out.println("pt "+recipeToBeSaved.getPrepTime());
        recipe.setPrepTime(recipeToBeSaved.getPrepTime());
        System.out.println(recipeToBeSaved.getServings());
        recipe.setServings(recipeToBeSaved.getServings());
        System.out.println(recipeToBeSaved.getDirections());
        recipe.setDirections(recipeToBeSaved.getDirections());
        System.out.println(recipeToBeSaved.getDifficulty());
        recipe.setDifficulty(recipeToBeSaved.getDifficulty());
        System.out.println("cats "+recipeToBeSaved.getCategories().size());
        recipe.setCategories(recipeToBeSaved.getCategories());


        /** this will be updated via its own forma and route **/
//        relationships
        System.out.println("imgs "+recipeToBeSaved.getImages().size());
        System.out.println("resIng "+recipeToBeSaved.getRecipeIngredients().size());
        /** --------------------- **/
//
//        recipe.setRecipeIngredients(recipeToBeSaved.getRecipeIngredients());
//        recipe.setImages(recipeToBeSaved.getImages());

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
    public @ResponseBody String deleteFile(@PathVariable long id){
        Image image = imageDao.getOne(id);
        if(image.getUrl() != null && image.getUrl().startsWith("https://s3")){
            s3.deleteFile(image.getUrl());
        }
        imageDao.deleteById(id);

        return "success";
    }

    @PostMapping("/recipes/{id}/publish")
    public @ResponseBody String publishRecipe(@PathVariable long id){
        Recipe recipe = recipeDao.getOne(id);
        recipe.setPublished(true);
        recipeDao.save(recipe);

        return "publish successful";
    }



   @PostMapping("/recipes/{id}/favorite")
    public String toggleFavorite(@PathVariable long id){
       User currentUser = userServ.loggedInUser();
       Recipe recipe = recipeDao.getOne(id);
       Favorite favorite = favDao.findByUserAndAndRecipe(currentUser,recipe);
       if(favorite != null){
           favDao.delete(favorite);
       }else{
           favDao.save(new Favorite(currentUser,recipe));
       }
       return "redirect:/recipes/" + id;
   }

}

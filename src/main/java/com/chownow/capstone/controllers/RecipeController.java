package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;

import com.chownow.capstone.services.RecipeService;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.chownow.capstone.services.AmazonService;
import org.springframework.security.core.context.SecurityContextHolder;
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

        System.out.println("term = " + term);
        List<Recipe> searchedRecipes = recipeDao.findAllByIsPublishedTrue();
        if (term != null) {
            searchedRecipes = new ArrayList<>();
            for (Recipe recipe : recipeDao.findAllByIsPublishedTrue()) {
                    if (recipe.getTitle().toLowerCase().contains(term.toLowerCase())) {
                        searchedRecipes.add(recipe);
                        continue;
                    }
                    String[] searchArray = term.replaceAll(", ", ",").split(",");
                    ArrayList<String> ingredientArray = new ArrayList<>();

                    recipe.getRecipeIngredients().forEach(ingredient -> {
                        ingredientArray.add(ingredient.getIngredient().getName());
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
        viewModel.addAttribute("recipeResultsDB", searchedRecipes);
        return "recipes/index";
    }

    /** Save Spoonacular API - Favorites **/
    @PostMapping("/recipes")
    public String saveRecipes(@ModelAttribute SpoonApiDto recipe, Model viewModel){

        String saveRecipe = recipeService.saveRecipe(recipe);

        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDao.getById(user1.getId());
        Optional<Recipe> existingRecipeEntity = recipeDao.findBySpoonApiId(recipe.getSpoonApiId());
        if (existingRecipeEntity.isPresent()) {
            Recipe isExisting = existingRecipeEntity.get();
            Favorite existingRecipeChef = favDao.findByUserAndAndRecipe(user, isExisting);

            if (existingRecipeChef != null) {
                return "redirect:/recipes/" + isExisting.getId();
            }
            favDao.save(new Favorite(user, isExisting));
            return "redirect:/recipes/" + isExisting.getId();
        }
        return "redirect:/dashboard";
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
        recipe.setTitle(recipeToBeSaved.getTitle());
        recipe.setDescription(recipeToBeSaved.getDescription());
        recipe.setCookTime(recipeToBeSaved.getCookTime());
        recipe.setPrepTime(recipeToBeSaved.getPrepTime());
        recipe.setServings(recipeToBeSaved.getServings());
        recipe.setDirections(recipeToBeSaved.getDirections());
        recipe.setDifficulty(recipeToBeSaved.getDifficulty());
        recipe.setCategories(recipeToBeSaved.getCategories());

        recipe = recipeDao.save(recipe);
        model.addAttribute("recipe", recipe);

        return "redirect:/recipes/" + recipe.getId() +"/edit";
    }

    @RequestMapping(value = "/recipes/{id}/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadRecipeImage(@PathVariable long id, @RequestParam("file") MultipartFile multipartFile,
                                               Model model){
        Recipe recipe = recipeDao.getOne(id);
        s3.uploadRecipeImage(multipartFile, recipe);
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
    public String publishRecipe(@PathVariable long id){
        Recipe recipe = recipeDao.getOne(id);
        recipe.setPublished(true);
        recipe = recipeDao.save(recipe);
        return "redirect:/recipes/" + recipe.getId();
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

    // RECIPE Images RETURNS PARTIAL
    @GetMapping("recipes/{id}/images")
    public String getImages(@PathVariable (value="id") long id, Model model){
        Recipe recipe = recipeDao.getOne(id);
        model.addAttribute("recipe",recipe);
        return "recipes/images :: recipeImages";
    }
}

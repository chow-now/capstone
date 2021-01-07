package com.chownow.capstone.services;

import com.chownow.capstone.models.SpoonApiDto;
import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class RefactoredRecipeService {
    @Autowired
    private RecipeRepository recipeDao;

    @Autowired
    private UserRepository userDao;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Here used transactional because we save data to 8 tables, during saving, if something happens it will rollback the data
     * @param recipe
     * @return
     **/
    @Transactional // Do this sequentially
    public String saveRecipe(SpoonApiDto recipe){
        // Hard-coded for now
        // User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDao.getUserByEmail("sahara.tijol@gmail.com");
        String cookTime = "";
        String prepTime = "";
        String servings = "";

        /** Handles cookTime when it's null from the front-end **/
        if (recipe.getCook().equals("")){
            cookTime = "0";
        }else{
            cookTime = recipe.getCook();
        }

        /** Handles prepTime when it's null from the front-end **/
        if (recipe.getPrep().equals("")){
            prepTime = "0";
        }else{
            prepTime = recipe.getPrep();
        }

        /** Handles servings when it's null from the front-end **/
        if (recipe.getServings().equals("")){
            servings = "0";
        }else{
            servings = recipe.getServings();
        }

        /** Create a recipe entity object to be saved **/
        Recipe recipeEntity = new Recipe();
        recipeEntity.setCookTime(Integer.parseInt(cookTime));
        recipeEntity.setDescription(recipe.getSummary());
        recipeEntity.setDifficulty("NA");
        recipeEntity.setDirections(recipe.getDirections());
        recipeEntity.setPrepTime(Integer.parseInt(prepTime));
        recipeEntity.setServings(Integer.parseInt(servings));
        recipeEntity.setTitle(recipe.getTitle());
        recipeEntity.setChef(user);

        recipeDao.save(recipeEntity); // Saves to recipe

        /** Loop through the recipe ingredients and add to db **/
        String ingredients[] = recipe.getIngredients().split("##");
        for (String ingredient: ingredients) {
            /** In the front-end there are many ingredients so they are coming to java end like below
            * Ingredient1@@Ingredient2@@Ingredient3@@Ingredient4@@Ingredient5@@
            * So we have to split using @@ to get the actual Ingredients from above array
            * that's why here used split method **/

            String ingredientObjects[] = ingredient.split("@@");

            /** Create a Ingredient entity object to be saved */
            //********* How are we checking if duplicate ingredient *******************************
            Ingredient ingredientEntity = ingredientRepository.save(new Ingredient(ingredientObjects[2]));

            /** Create a RecipeIngredient entity object to be saved */
            RecipeIngredient recipeIngredientEntity = new RecipeIngredient();
            recipeIngredientEntity.setAmount(Double.parseDouble(ingredientObjects[0]));
            recipeIngredientEntity.setUnit(ingredientObjects[1]);
            recipeIngredientEntity.setIngredient(ingredientEntity);
            recipeIngredientEntity.setRecipe(recipeEntity);

            recipeIngredientRepository.save(recipeIngredientEntity);
        }

         /**
         * In the UI there are many Categories so they are coming to java end like this:
         * Category1##Category2##Category3##Category4##Category5##
         *
         * So we have to split using ## to get the actual Ingredients from above array
         * that's why here used split method
         **/

        Set<Category> categoriesToSet = new HashSet<>();
        String categories[] = recipe.getCategories().split("##");
        for (String category : categories ) {
            /** Create a Category to save **/
            Category dbCategory = categoryRepository.save(new Category(category));
            categoriesToSet.add(dbCategory);
        }
        /** Create a Recipe Categories to be saved **/
        recipeEntity.setCategories(categoriesToSet);
        recipeDao.save(recipeEntity);
        /** Create a Favorites entity object to be saved **/
        Set<Recipe> userFavorites = user.getFavorites();
        userFavorites.add(recipeEntity);
        user.setFavorites(userFavorites);
        userDao.save(user);

        /** Create a Image entity object to be saved **/
        imageRepository.save(new Image(recipe.getImage(),recipeEntity));
        System.out.println("Recipe saved!!");
        return recipeEntity.getId() + "";
    }

    /**    RECIPE MATCHES BASED ON USER PANTRY ITEMS    **/
    public List<Recipe> getMatches(User user) {
        List<PantryIngredient> pantryIngredients = user.getPantry().getPantryIngredients();
        List<Recipe> recipes = recipeDao.findAll();

        List<Recipe> possibleRecipes = new ArrayList<>();

        ArrayList<Long> ingredientsToMap = new ArrayList<>();
        System.out.println("Ingredients in Users Pantry");
        for (PantryIngredient pi : pantryIngredients) {
            ingredientsToMap.add(pi.getIngredient().getId());
            System.out.println(pi.getIngredient().getId());
        }
        for (Recipe r : recipes) {
            boolean canMake = true;
            for (RecipeIngredient ri : r.getRecipeIngredients()) {
                if (!ingredientsToMap.contains(ri.getIngredient().getId())) {
                    System.out.println("Ingredient not in pantry :" + ri.getIngredient().getId());
                    canMake = false;
                    break;
                }
            }
            if (canMake) possibleRecipes.add(r);
        }
        return possibleRecipes;
    }

    /** RECIPE MOST FAVORITED **/
    public List<Recipe> getTopFavorites(){
        List<Recipe> recipes = recipeDao.findAll();
        List<Recipe> favorites = new ArrayList<>();

        for (Recipe r : recipes){
            if (r.getFavoritedBy().size() > 5 && favorites.size()<10){
                favorites.add(r);
            }
        }
        return favorites;
    }
}

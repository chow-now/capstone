package com.chownow.capstone.services;


import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

@Service
public class SeedRunner {
    @Autowired
    private UserRepository userDao;
    @Autowired
    private FollowRepository followDao;
    @Autowired
    private CategoryRepository catDao;
    @Autowired
    private IngredientRepository ingredientDao;
    @Autowired
    private RecipeRepository recipeDao;
    @Autowired
    private PantryRepository pantryDao;
    @Autowired
    private RecipeIngredientRepository recipeIngDao;
    @Autowired
    private PantryIngredientRepository pantryIngDao;
    @Autowired
    private ImageRepository imageDao;
    @Autowired
    private UserService userServ;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IngredientService ingServ;

    @Autowired
    private FavoriteRepository favDao;

    private final Faker faker = new Faker();

    private static final Logger LOGGER = LoggerFactory.getLogger(SeedRunner.class);
    boolean seederHasRun = false;

    @EventListener
    public void eventListener(ApplicationStartedEvent event){
        seederHasRun = userDao.getFirstByEmail("chef@chownow.com")!=null;
        if(!seederHasRun){
            LOGGER.info("Start Seeding");
            LOGGER.info("SEEDING CATEGORIES");
            seedCategories();
            LOGGER.info("CATEGORIES DONE");
            LOGGER.info("SEEDING INGREDIENTS");
            seedIngredients();
            LOGGER.info("INGREDIENTS DONE");
            LOGGER.info("SEEDING USERS");
            seedUsers();
            LOGGER.info("USERS DONE");
            LOGGER.info("SEEDING FOLLOWS");
            seedFollows();
            LOGGER.info("FOLLOWS DONE");
            LOGGER.info("SEEDING PANTRIES");
            seedPantries();
            LOGGER.info("PANTRIES DONE");
            LOGGER.info("SEEDING RECIPES");
            seedRecipes();
            LOGGER.info("RECIPES DONE");
            LOGGER.info("SEEDING PANTRY INGREDIENTS");
            //seedPantryIngredients();
            LOGGER.info("PANTRY INGREDIENTS DONE");
            LOGGER.info("SEEDING RECIPE INGREDIENTS");
            seedRecipeIngredients();
            LOGGER.info("RECIPE INGREDIENTS DONE");
            LOGGER.info("SEEDING FAVORITES");
            seedFavorites();
            LOGGER.info("FAVORITES DONE");
            LOGGER.info("SEEDING RECIPE CATEGORIES");
            seedRecipeCategories();
            LOGGER.info("RECIPE CATEGORIES DONE");
            LOGGER.info("SEEDING RECIPE IMAGES");
            seedRecipeImages();
            LOGGER.info("RECIPE IMAGES DONE");
            User initialUser = new User("chef@chownow.com","ChowNow","Password123!");
            initialUser.setPassword(passwordEncoder.encode(initialUser.getPassword()));
            initialUser.setAdmin(true);
            initialUser.setAboutMe(faker.buffy().quotes());
            initialUser.setAvatar("/img/chef-avatar.jpg");
            userDao.save(initialUser);

        }else{
            LOGGER.info("Seeder has already run");
        }
        LOGGER.info("Seeding Complete");
    }

    public void seedUsers(){
        for(int i = 0; i<=10; i++){
            String firstName = faker.name().firstName();
            LOGGER.info("firstName = " + firstName);
            String email = faker.internet().emailAddress();
            LOGGER.info("email = " + email);
            String password = "Password123!";
            LOGGER.info("password = " + password);
            User seedUser = new User(email,firstName,password);
            seedUser.setPassword(passwordEncoder.encode(seedUser.getPassword()));
            seedUser.setAboutMe(faker.buffy().quotes());
            seedUser.setAvatar(randomAvatar());
            userDao.save(seedUser);
        }
    }

    public void seedFollows() {
        int usersSize = userDao.findAll().size();
        for(long i = 1; i<=usersSize; i++) {
            /* get an id != user i */
            long id = i;
            do{
                id = faker.number().numberBetween(1,usersSize+1);
            }while(id == i);
            /* user i will follow user with id */
            Follow follow = new Follow(
                    userDao.getOne(i),
                    userDao.getOne(id)
            );
            followDao.save(follow);
        }
    }

    public void seedCategories(){
        List<String> categories = new ArrayList<>();
        categories.add("African");
        categories.add("American");
        categories.add("British");
        categories.add("Cajun");
        categories.add("Chinese");
        categories.add("Caribbean");
        categories.add("European");
        categories.add("French");
        categories.add("German");
        categories.add("Greek");
        categories.add("Indian");
        categories.add("Irish");
        categories.add("Italian");
        categories.add("Japanese");
        categories.add("Jewish");
        categories.add("Korean");
        categories.add("Latin");
        categories.add("Mediterranean");
        categories.add("Mexican");
        categories.add("Middle Eastern");
        categories.add("Southern");
        categories.add("Spanish");
        categories.add("Thai");
        categories.add("Vietnamese");
        for(String name : categories){
            LOGGER.info(name);
            catDao.save(new Category(name));
        }
    }

    public void seedIngredients(){
        getIngredients();
    }

    public void seedPantries(){
        int usersSize = userDao.findAll().size();
        for(long i = 1; i<=usersSize; i++){
            Pantry pantry = new Pantry(userDao.getOne(i));
            pantryDao.save(pantry);
        }
    }

    public void seedPantryIngredients(){
        int pantriesSize = pantryDao.findAll().size();
        int ingredientsSize = ingredientDao.findAll().size();
        for (long i = 1; i <= pantriesSize; i++){
            int j = 0;
            int max = faker.number().numberBetween(1,25);
            while (j < max){
                long id = faker.number().numberBetween(1,ingredientsSize+1);
                List<String> amountUnit = Arrays.asList(faker.food().measurement().split(" "));
                double amount = 2.50;
                LOGGER.info("amount = " + amount);
                String unit = amountUnit.get(1);
                LOGGER.info("unit = " + unit);
                PantryIngredient pi = new PantryIngredient(
                        amount,
                        unit,
                        pantryDao.getOne(i),
                        ingredientDao.getOne(id)
                );
                pantryIngDao.save(pi);
                j++;
            }
        }
    }

    public void seedRecipes(){
        /* preset difficulty levels */
        List<String> levels = new ArrayList<>();
        levels.add("easy");levels.add("medium");levels.add("hard");
        int usersSize = userDao.findAll().size();
        for(long i = 1; i<=60; i++){
            long spoonApiId = faker.number().numberBetween(0, 999);
            LOGGER.info("title = " + spoonApiId);
            String title= faker.food().dish();
            LOGGER.info("title = " + title);
            String description = faker.friends().quote();
            LOGGER.info("description = " + description);
            String directions = faker.buffy().quotes();
            LOGGER.info("directions = " + directions);
            String difficulty = levels.get(faker.number().numberBetween(0,3));
            LOGGER.info("difficulty = " + difficulty);
            int cooktime = faker.number().numberBetween(1,999);
            LOGGER.info("cooktime = " + cooktime);
            int preptime = faker.number().numberBetween(1,999);
            LOGGER.info("preptime = " + preptime);
            int servings = faker.number().numberBetween(1,20);
            LOGGER.info("servings = " + servings);
            Recipe recipe = new Recipe(
                    spoonApiId,
                    title,
                    description,
                    directions,
                    difficulty,
                    cooktime,
                    preptime,
                    servings,
                    userDao.getOne((long) faker.number().numberBetween(1,usersSize+1))
            );
            if(i%2==0){
                recipe.setPublished(true);
            }
            recipeDao.save(recipe);
        }
    }

    public void seedRecipeIngredients(){
        int recipesSize = recipeDao.findAll().size();
        int ingredientsSize = ingredientDao.findAll().size();
        for (long i = 1; i <= recipesSize; i++){
            int j = 0;
            int max = faker.number().numberBetween(1,11);
            while (j < max){
                long id = faker.number().numberBetween(1,ingredientsSize+1);
                List<String> amountUnit = Arrays.asList(faker.food().measurement().split(" "));
                double amount = 2.25;
                LOGGER.info("amount = " + amount);
                String unit = amountUnit.get(1);
                LOGGER.info("unit = " + unit);
                RecipeIngredient ri = new RecipeIngredient(
                        amount,
                        unit,
                        recipeDao.getOne(i),
                        ingredientDao.getOne(id)
                );
                recipeIngDao.save(ri);
                j++;
            }
        }
    }

    public void seedRecipeCategories(){
        int categoriesSize = catDao.findAll().size();
        int recipesSize = recipeDao.findAll().size();

        for(long i = 1; i <= recipesSize; i++){
            Recipe recipe = recipeDao.getFirstById(i);
            int max = faker.number().numberBetween(0,4);
            Set<Category> categories = new HashSet<Category>();
            for(int j = 0;j < max; j++){
                long index = faker.number().numberBetween(1,categoriesSize);
                categories.add(catDao.getOne(index));
            }
            for(Category c :categories){
                System.out.println(c.getId());
            }
            recipe.setCategories(categories);
            recipeDao.save(recipe);
        }
    }

    public void seedRecipeImages(){
        int recipesSize = recipeDao.findAll().size();

        for(long i = 1; i <= recipesSize; i++){
            Recipe recipe = recipeDao.getFirstById(i);
            int max = faker.number().numberBetween(0,4);
            String word = recipe.getTitle().split(" ")[0];
            word = toEnglish(word);
            for(int j = 0;j < max; j++){
                Image image = new Image(randomImg(word),recipe);
                imageDao.save(image);
            }
        }
    }

    public void seedFavorites(){
        int usersSize = userDao.findAll().size();
        int recipesSize = recipeDao.findAll().size();

        for(long i = 2; i<=recipesSize; i+=2){
            Recipe recipe = recipeDao.getFirstById(i);
            LOGGER.info(recipe.getTitle());
            int max = faker.number().numberBetween(1,10);
            for(int j = 0;j < max; j++){
                long index = faker.number().numberBetween(1,usersSize+1);
                User user = userDao.getOne(index);
                favDao.save(new Favorite(user,recipe));
            }
        }
    }

    public String makeSingular(String word){
        if(word.endsWith("s")){
            word = StringUtils.chop(word);
        }
        return word;
    }

    public double makeDouble(String amount) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        double convertedString = 0;
        try {
            if(engine.eval(amount) instanceof String){
                amount = (String) engine.eval(amount);
                convertedString = Double.parseDouble(amount);
            }
            if(engine.eval(amount) instanceof Double){
                convertedString = (double) engine.eval(amount);
            }
            if(engine.eval(amount) instanceof Integer){
                convertedString = (int) engine.eval(amount);
            }
        }catch (ScriptException e) {
            e.printStackTrace();
        }
        return convertedString;
    }

    public String randomAvatar() {
        Random rand = new Random();
        int num = rand.nextInt(((90 - 1) + 1)) + 1;
        int toss = rand.nextInt(((2 - 1) + 1)) + 1;
        String baseUrl = "https://randomuser.me/api/portraits/";
        return toss >1 ? baseUrl+"men/"+ num +".jpg": baseUrl+"women/"+ num +".jpg";
    }

    public String randomImg(String word){
        String img = "https://loremflickr.com/800/600/"+word;
        LOGGER.info("img = " + img);
        return img;
    }

    public void getIngredients(){
        ingServ.createIngredients();

    }

    public String toEnglish(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

}

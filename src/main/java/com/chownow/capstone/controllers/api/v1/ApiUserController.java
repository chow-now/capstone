package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.FollowRepository;
import com.chownow.capstone.repos.PantryRepository;
import com.chownow.capstone.repos.RecipeRepository;
import com.chownow.capstone.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private PantryRepository pantryDao;

    @Autowired
    private RecipeRepository recipeDao;

    @Autowired
    private FollowRepository followDao;

    /* GET MAPPINGS */
    // get all users
    @GetMapping
    public @ResponseBody List<User> getAllUsers(){
        return userDao.findAll();
    }

    // get user by id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable (value="id") long userId) {
        User user = userDao.getById(userId);
        if(user == null){
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }

    // get user recipes
    @GetMapping("/{id}/recipes")
    public @ResponseBody List<Recipe> getUserRecipes(@PathVariable (value="id") long userId) {
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()){
            User dbUser = user.get();
            return dbUser.getRecipes();
        }
        return null;
    }

    // get user pantry
    @GetMapping("/{id}/pantry")
    public @ResponseBody Pantry getPantryInventory(@PathVariable (value="id") long userId){
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()){
            User dbUser = user.get();
            return pantryDao.getFirstByOwner(dbUser);
        }
        return null;
    }

    // get user followers
    @GetMapping("/{id}/followers")
    @ResponseBody
    public List<User> getFollowers(@PathVariable (value="id") long userId){
        List<User> followers = new ArrayList<>();
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()){
            User dbUser = user.get();
            List<Follow> followResults =followDao.findAllByUser(dbUser);
            for(Follow follow : followResults){
                followers.add(follow.getUser());
            }
            return followers;
        }
        return null;
    }

    //get user following

    @GetMapping("/{id}/following")
    @ResponseBody
    public List<User> getFollowings(@PathVariable (value="id") long userId){
        List<User> followings = new ArrayList<>();
        Optional<User> user = userDao.findById(userId);
        if (user.isPresent()) {
            User dbUser = user.get();
            List<Follow> followResults = followDao.findAllByFriend(dbUser);
            for (Follow follow : followResults) {
                followings.add(follow.getFriend());
            }
            return followings;
        }
        return null;
    }


    // DB recipes that the user can make with items in their pantry
    @GetMapping("/{id}/matches")
    @ResponseBody
    public List<Recipe> getMatches(@PathVariable (value="id") long userId){
        User user = userDao.getById(userId);
        List<PantryIngredient> pantryIngredients = user.getPantry().getPantryIngredients();
        List<Recipe> recipes = recipeDao.findAll();

        List<Recipe> possibleRecipes = new ArrayList<>();

        ArrayList<Long> ingredientsToMap= new ArrayList<>();
        System.out.println("Ingredients in Users Pantry");
        for(PantryIngredient pi : pantryIngredients){
            ingredientsToMap.add(pi.getIngredient().getId());
            System.out.println(pi.getIngredient().getId());
        }
        for(Recipe r : recipes){
            boolean canMake = true;
            for(RecipeIngredient ri : r.getRecipeIngredients()){
                if(!ingredientsToMap.contains(ri.getIngredient().getId())){
                    System.out.println("Ingredient not in pantry :" +ri.getIngredient().getId());
                    canMake = false;
                    break;
                }
            }
            if(canMake) possibleRecipes.add(r);
        }
        return possibleRecipes;
    }

    /* POST MAPPINGS FOR CRUD */

    // create user
    @PostMapping("/add")
    public User addUser(@RequestBody User newUser){
        return userDao.save(newUser);
    }

    // update user
    @PostMapping("/{id}/edit")
    public User updateUser(@RequestBody User requestUser,@PathVariable (value = "id")long userId){
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()) {
            User dbUser = user.get();
            dbUser.setFirstName(requestUser.getFirstName());
            dbUser.setEmail(requestUser.getEmail());
            return userDao.save(dbUser);
        }
        return null;
    }

    // delete user
    @PostMapping("/{id}/delete")
    public ResponseEntity<User> deleteUser(@PathVariable ("id") long userId){
        
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()) {
            User dbUser = user.get();
            try{
                userDao.delete(dbUser);
                return ResponseEntity.ok().build();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return  null;
    }


}

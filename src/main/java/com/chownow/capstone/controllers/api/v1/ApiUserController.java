package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.Follow;
import com.chownow.capstone.models.Pantry;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.FollowRepository;
import com.chownow.capstone.repos.PantryRepository;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private FollowRepository followDao;

    /* GET MAPPINGS */

    // get all users
    @GetMapping
    public @ResponseBody List<User> getAllUsers(){
        return userDao.findAll();
    }
    
    // get user by id
    @GetMapping("/{id}")
    public @ResponseBody User getUser(@PathVariable (value="id") long userId) {
        Optional<User> user = userDao.findById(userId);
        return user.orElse(null);
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
    public @ResponseBody List<User> getFollowers(@PathVariable (value="id") long userId){
        List<User> followers = new ArrayList<>();
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()){
            User dbUser = user.get();
            List<Follow> followResults =followDao.findAllByFollowee(dbUser);
            for(Follow follow : followResults){
                followers.add(follow.getFollower());
            }
            return followers;
        }
        return null;
    }

    //get user following
    @GetMapping("/{id}/following")
    public @ResponseBody List<User> getFollowings(@PathVariable (value="id") long userId){
        List<User> followings = new ArrayList<>();
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()){
            User dbUser = user.get();
            List<Follow> followResults =followDao.findAllByFollower(dbUser);
            for(Follow follow : followResults){
                followings.add(follow.getFollowee());
            }
            return followings;
        }
        return null;
    }

    /* POST MAPPINGS FOR CRUD */

    // create user
    @PostMapping("")
    public @ResponseBody User createUser(@RequestBody User user){
        return userDao.save(user);
    }

    // update user
    @PutMapping("/{id}")
    public @ResponseBody User updateUser(@RequestBody User requestUser,@PathVariable (value = "id")long userId){
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()) {
            User dbUser = user.get();
            dbUser.setFirstName(requestUser.getFirstName());
            dbUser.setEmail(requestUser.getEmail());
            dbUser.setLastName(requestUser.getLastName());
            return userDao.save(dbUser);
        }
        return null;
    }

    // delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable ("id") long userId){
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()) {
            User dbUser = user.get();
            userDao.delete(dbUser);
        }
        return ResponseEntity.ok().build();
    }
}

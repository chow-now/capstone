package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.Follow;
import com.chownow.capstone.models.Pantry;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.FollowRepository;
import com.chownow.capstone.repos.PantryRepository;
import com.chownow.capstone.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiUserController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private PantryRepository pantryDao;

    @Autowired
    private FollowRepository followDao;

    /* GET MAPPINGS */
    // get all users
    @GetMapping("/users")
    public @ResponseBody List<User> getAllUsers(){
        return userDao.findAll();
    }

    // get user by id
    @GetMapping("user/{id}")
    public ResponseEntity<User> getUser(@PathVariable (value="id") long userId) {
        User user = userDao.getById(userId);
        if(user == null){
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }

    // get user recipes
    @GetMapping("user/{id}/recipes")
    public @ResponseBody List<Recipe> getUserRecipes(@PathVariable (value="id") long userId) {
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()){
            User dbUser = user.get();
            return dbUser.getRecipes();
        }
        return null;
    }

    // get user pantry
    @GetMapping("user/{id}/pantry")
    public @ResponseBody Pantry getPantryInventory(@PathVariable (value="id") long userId){
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()){
            User dbUser = user.get();
            return pantryDao.getFirstByOwner(dbUser);
        }
        return null;
    }

    // get user followers
    @GetMapping("user/{id}/followers")
    @ResponseBody
    public List<User> getFollowers(@PathVariable (value="id") long userId){
        List<User> followers = new ArrayList<>();
        Optional<User> user = userDao.findById(userId);
        if(user.isPresent()){
            User dbUser = user.get();
<<<<<<< HEAD
            List<Follow> followResults = followDao.findAllByFriend(dbUser);
            for(Follow follow : followResults){
                followers.add(follow.getFriend());
=======
            List<Follow> followResults =followDao.findAllByUser(dbUser);
            for(Follow follow : followResults){
                followers.add(follow.getUser());
>>>>>>> main
            }
            return followers;
        }
        return null;
    }

    //get user following
<<<<<<< HEAD
    @GetMapping("/{id}/following")
    public @ResponseBody List<User> getFollowings(@PathVariable (value="id") long userId) {
=======
    @GetMapping("user/{id}/following")
    @ResponseBody
    public List<User> getFollowings(@PathVariable (value="id") long userId){
>>>>>>> main
        List<User> followings = new ArrayList<>();
        Optional<User> user = userDao.findById(userId);
        if (user.isPresent()) {
            User dbUser = user.get();
<<<<<<< HEAD
            List<Follow> followResults = followDao.findAllByFriend(dbUser);
            for (Follow follow : followResults) {
=======
            List<Follow> followResults =followDao.findAllByFriend(dbUser);
            for(Follow follow : followResults){
>>>>>>> main
                followings.add(follow.getFriend());
            }
            return followings;
        }
        return null;
    }

    /* POST MAPPINGS FOR CRUD */
    @PostMapping("user/test")
    public Object testing(@RequestBody Object object){
        return object;
    }
    // create user
    @PostMapping("user/add")
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
            dbUser.setLastName(requestUser.getLastName());
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

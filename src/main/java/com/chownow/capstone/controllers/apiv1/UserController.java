package com.chownow.capstone.controllers.apiv1;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userDao;

    // get all users
    @GetMapping
    public List<User> getAllUsers(){
        return userDao.findAll();
    }
    
    // get user by id
    @GetMapping("/{id}")
    public User getUser(@PathVariable (value="id") long userId) throws Exception {
        return userDao.findById(userId).orElseThrow( () -> new Exception("User not found with id: "+ userId));
    }

    // create user
    @PostMapping("")
    public User createUser(@RequestBody User user){
        return userDao.save(user);
    }
    // update user
    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user,@PathVariable (value = "id")long userId) throws Exception {
         User existing = userDao.findById(userId).orElseThrow( () -> new Exception("User not found with id: "+ userId));
         existing.setFirstName(user.getFirstName());
         existing.setEmail(user.getEmail());
         existing.setLastName(user.getLastName());
         return userDao.save(existing);
    }
    // delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable ("id") long userId) throws Exception {
        User existing = userDao.findById(userId).orElseThrow( () -> new Exception("User not found with id: "+ userId));
        userDao.delete(existing);
        return ResponseEntity.ok().build();
    }
}

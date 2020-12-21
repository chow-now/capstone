package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.FollowRepository;
import com.chownow.capstone.repos.IngredientRepository;
import com.chownow.capstone.repos.PantryIngredientRepository;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userDao;
    @Autowired
    private FollowRepository followDao;

    @Autowired
    private IngredientRepository ingredientDao;

    @Autowired
    PantryIngredientRepository pantryIngDao;

    // CREATE USER

    @GetMapping("/create")
    public String createUser(Model model){
        model.addAttribute("user",new User());
        return "users/new";
    }

    @PostMapping("/create")
    public String submitUser(
                @Valid @ModelAttribute User user,
                Errors validation,
                Model model
    ) {
            if(validation.hasErrors()){
                model.addAttribute("errors",validation);
                model.addAttribute("user",user);
                return "recipes/new";
            }
            user.setAdmin(false);
            userDao.save(user);
            return "profile";
    }

    // READ ALL USERS
    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", userDao.findAll());
        return "users/index";
    }

    // READ 1 USER
    @GetMapping("/{id}")
    public String showUserProfile(@PathVariable long id, Model model){
        /*Get user*/
        User currentUser = userDao.getOne(2L);
        User user = userDao.getOne(id);
        model.addAttribute("user",user);
        if(followDao.findByUserAndFriend(currentUser,user) != null){
            model.addAttribute("isFollowing",true);
        }
        return "users/profile";
    }

    // SHOW USER EDIT FORM
    @GetMapping("/{id}/edit")
    public String showEditUser(@PathVariable long id, Model model){
        model.addAttribute("user",userDao.getOne(id));
        return "/users/edit";
    }

    // POST MAPPING FOR USER RELATED TO PROFILE DASHBOARD

//	@PostMapping("/users/{id}/edit")
//	public String editRecipe(User userToBeSaved) {
//		User userDb = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		userToBeSaved.setUser(userDb); /*Todo: make setUser*/
//		userDao.save(userToBeSaved);
//		return "redirect:/user";
//	}


    // SUBMIT USER EDIT FORM
    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable long id, Model model){
        model.addAttribute("user",userDao.getOne(id));
        return "redirect:/users/edit";
    }

    // DELETE USER
    @PostMapping("/{id}/delete")
    public String deleteAd(@PathVariable long id){
        userDao.deleteById(id);
        return "redirect:/";
    }


    // Create a follow request
    @RequestMapping(value = "/follow", method = RequestMethod.POST, headers="Content-Type=application/json")
    public @ResponseBody Follow postFollow(@RequestBody AjaxFollowRequest ajaxFollowRequest) {
        User currentUser = userDao.getById(2L);
        User friend = userDao.getById(ajaxFollowRequest.getFriendId());
        Follow dbFollow = null;
        if(followDao.findByUserAndFriend(currentUser,friend) == null){
            dbFollow = followDao.save(new Follow(currentUser,friend));
        }
        return dbFollow;
    }


    // CREATE A NEW PANTRY ITEM
    @RequestMapping(value = "/pantry/ingredient/new", method = RequestMethod.POST, headers="Content-Type=application/json")
    public @ResponseBody String postPantryIngredient(@RequestBody AjaxPantryIngredientRequest pantryIngredient){
        User currentUser = userDao.getOne(2L);
        Ingredient dbIngredient = null;
        boolean isNotInDb = true;
        for(Ingredient i : ingredientDao.findAllByNameLike(pantryIngredient.getName())){
            if(pantryIngredient.getName().equalsIgnoreCase(i.getName())){
                dbIngredient = i;
                isNotInDb = false;
                break;
            }
        }
        if(isNotInDb){
            dbIngredient = ingredientDao.save(new Ingredient(pantryIngredient.getName().toLowerCase()));
        }
        PantryIngredient newPantryIng = new PantryIngredient(
                pantryIngredient.getAmount(),
                pantryIngredient.getUnit(),
                currentUser.getPantry(),
                dbIngredient
        );
        pantryIngDao.save(newPantryIng);
        return "im done";
    }
}

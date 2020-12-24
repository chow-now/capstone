package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;
import com.chownow.capstone.services.AmazonService;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;

import java.util.Map;


@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private AmazonService s3;

    @Autowired
    private FollowRepository followDao;

    @Autowired
    private IngredientRepository ingredientDao;

    @Autowired
    PantryIngredientRepository pantryIngDao;

    @Autowired
    PantryRepository pantryDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userServ;

    // CREATE USER

    @GetMapping("/create")
    public String createUser(Model model) {
        model.addAttribute("user", new User());
        return "users/new";
    }

    @PostMapping("/create")
    public String submitUser(
            @Valid @ModelAttribute User user,
            Errors validation,
            Model model
    ) {
        User existingEmail = userDao.getFirstByEmail(user.getEmail());

        if(existingEmail != null){
            validation.rejectValue("email", "user.email", "Duplicate email " + user.getEmail());
        }

        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("user", user);
            return "users/new";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAdmin(false);
        User dbUser = userDao.save(user);

        userServ.authenticate(dbUser);
        System.out.println(dbUser.getFirstName());
        System.out.println(dbUser.getId());
        Pantry pantry = new Pantry(dbUser);
        Pantry dbPantry = pantryDao.save(pantry);
        System.out.println(dbPantry.getId());
        System.out.println(dbPantry.getOwner().getId());
        model.addAttribute(dbUser);
        return "users/profile";
    }

    // READ ALL USERS
    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", userDao.findAll());
        return "users/index";
    }

    // READ 1 USER
    @GetMapping("/{id}")
    public String showUserProfile(@PathVariable long id, Model model) {
        /*Get user*/
        User currentUser = userDao.getOne(2L);
        User user = userDao.getOne(id);
        model.addAttribute("user", user);
        if (followDao.findByUserAndFriend(currentUser, user) != null) {
            model.addAttribute("isFollowing", true);
        }
        return "users/profile";
    }

    // SHOW USER EDIT FORM
    @GetMapping("/{id}/edit")
    public String showEditUser(@PathVariable long id, Model model) {
        model.addAttribute("user", userDao.getOne(id));
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
    public String editUser(@PathVariable(name="id") long id, @Valid User editUser,Errors validation,Model model) {
        User user = userDao.getOne(id);
        if (validation.hasErrors()) {
            System.out.println("has errors");
            System.out.println(validation);
            model.addAttribute("errors", validation);
            model.addAttribute("user", editUser);
            return "users/edit";
        }
        user.setFirstName(editUser.getFirstName());
        user.setEmail(editUser.getEmail());
        user = userDao.save(user);
        model.addAttribute("user",user);
        return "redirect:/users/"+id;
    }

    // update password
    @PostMapping("/{id}/reset")
    public String resetPassword(@PathVariable(name="id") long id, @RequestParam Map<String, String> params,Model model){
        User user = userDao.getOne(id);
        // regex for password
        String passValidation = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        String password = params.get("password");
        String newPassword = params.get("new_password");
        String passwordConfirmation = params.get("confirm_password");
        boolean canUpdate = true;
        // if new password and confirmation dont match set error
        if(!newPassword.equals(passwordConfirmation)){
            System.out.println("passwords dont match");
            canUpdate = false;
            model.addAttribute("notMatch", true );
        }
        // if password fails regex set error
        if(!newPassword.matches(passValidation)){
            System.out.println("invalid password");
            canUpdate = false;
            model.addAttribute("invalidPassword",true);
        }
        // if errors redirect to edit page
        if(!canUpdate){
            System.out.println("cant update");
            model.addAttribute("user",user);
            return "users/edit";
        }
        //reset password
        user.setPassword(newPassword);
        model.addAttribute("user",userDao.save(user));
        return "redirect:/users/"+id;
    }

    // SUBMIT UPLOAD USER AVATAR FORM

    @PostMapping("/{id}/upload")
    public String uploadAvatar(@PathVariable long id, @RequestParam(value="file") MultipartFile multipartFile, Model model){
        User user = userDao.getOne(id);
        if(user.getAvatar() != null && user.getAvatar().startsWith("https://s3")){
            s3.deleteFile(user.getAvatar());
        }
        s3.uploadAvatar(multipartFile, user);
        model.addAttribute("user",user);
        return "redirect:/users/"+user.getId()+"/edit";
    }

    // DELETE USER
    @PostMapping("/{id}/delete")
    public String deleteAd(@PathVariable long id) {
        userDao.deleteById(id);
        return "redirect:/";
    }


    // Create a follow request
    @RequestMapping(value = "/follow", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public @ResponseBody
    Follow postFollow(@RequestBody AjaxFollowRequest ajaxFollowRequest) {
        User currentUser = userDao.getById(2L);
        User friend = userDao.getById(ajaxFollowRequest.getFriendId());
        Follow dbFollow = null;
        if (followDao.findByUserAndFriend(currentUser, friend) == null) {
            dbFollow = followDao.save(new Follow(currentUser, friend));
        }
        return dbFollow;
    }


    // CREATE A NEW PANTRY ITEM
    @RequestMapping(value = "/pantry/ingredient/new", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public @ResponseBody
    String postPantryIngredient(@RequestBody AjaxPantryIngredientRequest pantryIngredient) {
        User currentUser = userDao.getOne(2L);
        Ingredient dbIngredient = null;
        boolean isNotInDb = true;
        for (Ingredient i : ingredientDao.findAllByNameLike(pantryIngredient.getName())) {
            if (pantryIngredient.getName().equalsIgnoreCase(i.getName())) {
                dbIngredient = i;
                isNotInDb = false;
                break;
            }
        }
        if (isNotInDb) {
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

    // EDIT PANTRY ITEM
    @RequestMapping(value = "/pantry/ingredient/edit", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public @ResponseBody
    String editPantryIngredient(@RequestBody AjaxPantryIngredientRequest pantryIngredient) {
        PantryIngredient pi = pantryIngDao.getOne(pantryIngredient.getId());
        pi.setAmount(pantryIngredient.getAmount());
        pi.setUnit(pantryIngredient.getUnit());
        pantryIngDao.save(pi);
        return "update complete";
    }

//    public static void main(String[] args) {
//        String passValidation = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
//        System.out.println("Iotaline03!".matches(passValidation));
//    }
}

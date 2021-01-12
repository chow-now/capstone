package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;
import com.chownow.capstone.services.AmazonService;
import com.chownow.capstone.services.RecipeService;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@RequestMapping
public class UserController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private AmazonService s3;

    @Autowired
    private FollowRepository followDao;
    @Autowired
    private RecipeRepository recipeDao;

    @Autowired
    private PantryRepository pantryDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userServ;

    @Autowired
    private RecipeService recipeServ;

    // CREATE USER
    @GetMapping("/register")
    public String registerUser(Model model) {
        if(userServ.isLoggedIn()){
            return "redirect:/recipes";
        }
        model.addAttribute("user", new User());
        return "users/new";
    }

    @PostMapping("/register")
    public String submitUser(
            @Valid @ModelAttribute User user,
            Errors validation,
            Model model
    ) {
        // validate if email already exists in db
        User existingEmail = userDao.getFirstByEmail(user.getEmail());
        if(existingEmail != null){
            validation.rejectValue("email", "user.email", "Duplicate email " + user.getEmail());
        }
        // user model validations
        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("user", user);
            return "users/new";
        }
        // encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // add fields to the existing user
        user.setAdmin(false);
        user.setAvatar("/img/chef-avatar.jpg");
        // save th user to db
        User dbUser = userDao.save(user);
        // create pantry for the user
        Pantry pantry = new Pantry(dbUser);
        pantryDao.save(pantry);
        // login the registered user
        userServ.authenticate(dbUser);
        model.addAttribute(dbUser);
        return "redirect:/users/"+dbUser.getId()+"/edit";
    }

    // PARTIAL REGISTER FORM
    @GetMapping("/register-form")
    public String getRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "users/forms :: register";
    }

    // GET USER PROFILE BY ID
    @GetMapping("/users/{id}")
    public String showUserProfile(@PathVariable long id, Model model) {
        // logged in user
        User currentUser = userServ.loggedInUser();
        // profile owner
        User user = userDao.findFirstById(id);
        // if profile page for user doesnt exist send to recipes index
        if(user == null){
            return "redirect:/recipes";
        }
        boolean isFollowing = false;
        // check if logged in user is following the profile owner
        if (followDao.findByUserAndFriend(currentUser, user) != null) {
            isFollowing = true;
        }
        model.addAttribute("user", user);
        // check if logged in user is the profile owner
        model.addAttribute("isOwner",userServ.isOwner(user));
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("drafts",recipeDao.findAllByChefAndIsPublishedFalse(user));
        model.addAttribute("published",recipeDao.findAllByChefAndIsPublishedTrue(user));

        return "users/profile";
    }

    // GET LOGGED IN USER DASHBOARD
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        User currentUser = userServ.loggedInUser();
        List<Recipe> currentSuggestions = recipeServ.getMatches(currentUser);
        if(currentUser.getAdmin()){
            return "redirect:/admin";
        }
        if(currentSuggestions.size()<currentUser.getSuggestedCount()){
            currentUser.setSuggestedCount(currentSuggestions.size());
            userDao.save(currentUser);
        }
        model.addAttribute("isFollowing", true);
        model.addAttribute("user", currentUser);
        model.addAttribute("isOwner",true);
        model.addAttribute("notificationCount",currentSuggestions.size());
        model.addAttribute("drafts",recipeDao.findAllByChefAndIsPublishedFalse(currentUser));
        model.addAttribute("published",recipeDao.findAllByChefAndIsPublishedTrue(currentUser));

        return "users/profile";
    }

    // SHOW USER EDIT FORM
    @GetMapping("/users/{id}/edit")
    public String showEditUser(@PathVariable long id, Model model) {
        User user = userDao.getOne(id);
        // restrict access to owner redirects others
        if(!userServ.isOwner(user)){
            return "redirect:/recipes";
        }
        // do not allow Oauth users to have password reset
        if(user.getAuthProvider()==null){
            model.addAttribute("canChange",true);
        }
        model.addAttribute("user",user);
        return "users/edit";
    }

    // POST MAPPING FOR USER RELATED TO PROFILE DASHBOARD

    // SUBMIT USER EDIT FORM
    @PostMapping("/users/{id}/edit")
    public String editUser(
            @PathVariable(name="id") long id,
            @Valid User editUser,
            Errors validation,
            Model model) {

        User user = userDao.getOne(id);
        // user model validations
        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("user", editUser);
            return "users/edit";
        }
        // add fields to the existing user
        user.setFirstName(editUser.getFirstName());
        user.setEmail(editUser.getEmail());
        user.setAboutMe(editUser.getAboutMe());
        // submit update
        user = userDao.save(user);
        model.addAttribute("user",user);
        return "redirect:/users/"+id;
    }

    // UPDATE PASSWORD
    @PostMapping("/users/{id}/reset")
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        model.addAttribute("user",userDao.save(user));
        return "redirect:/users/"+id;
    }

    // UPLOAD USER AVATAR
    @PostMapping("/users/{id}/upload")
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
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable long id) {
        userDao.deleteById(id);
        return "redirect:/";
    }

    // Create a follow request
    @RequestMapping(value = "/users/follow", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public @ResponseBody
    String toggleFollow(@RequestBody AjaxFollowRequest ajaxFollowRequest) {
        User currentUser = userServ.loggedInUser();
        User friend = userDao.getById(ajaxFollowRequest.getFriendId());
        Follow follow = followDao.findByUserAndFriend(currentUser, friend);
        if (follow == null) {
            followDao.save(new Follow(currentUser, friend));
        }else{
            followDao.delete(follow);
        }
        return "done";
    }

    // USER RECIPE MATCHES RETURNS PARTIAL
    @GetMapping("users/{id}/matches")
    public String getMatches(@PathVariable (value="id") long userId, Model model){
        User user = userDao.getById(userId);
        List<Recipe> suggestions =  recipeServ.getMatches(user);
        model.addAttribute("suggestions",suggestions);
        model.addAttribute("user",user);
        return "users/suggestions :: suggestions";
    }

    // UPDATE RECIPE MATCHES FOR NOTFICATIONS
    @PostMapping("users/{id}/matches/update")
    public @ResponseBody
    Integer updateCount(@PathVariable (value="id") long userId){
        User user = userDao.getById(userId);
        List<Recipe> suggestions =  recipeServ.getMatches(user);
        user.setSuggestedCount(suggestions.size());
        userDao.save(user);
        return user.getSuggestedCount();
    }
}

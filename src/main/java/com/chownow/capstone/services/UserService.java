package com.chownow.capstone.services;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    UserRepository userDao;

    @Autowired
    private RecipeRepository recipeDao;

    @Autowired
    private PantryRepository pantryDao;

    @Autowired
    private CategoryRepository catDao;

    @Autowired
    private IngredientRepository ingredientDao;

    @Autowired
    private RecipeIngredientRepository recipeIngDao;

    @Autowired
    private PantryIngredientRepository pantryIngDao;

    @Autowired
    private  ImageRepository imageDao;

    @Autowired
    private FavoriteRepository favDao;

    @Autowired
    private FollowRepository followDao;

    @Autowired
    private RecipeService recipeServ;

    @Autowired
    private PantryIngredientRepository panIngDao;
    @Autowired
    private AmazonService s3;


    public boolean isLoggedIn() {
        boolean isAnonymousUser =
                SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
        return ! isAnonymousUser;
    }

    // Returns a user obj from db if spring security  session not anonymous
    public User loggedInUser() {
        if (! isLoggedIn()) {
            return null;
        }
        User sessionUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDao.getOne(sessionUser.getId());
    }

    // Checks if the user is the owner of the recipe
    public boolean isOwner(User user){
        boolean isOwner = false;
        isOwner = isLoggedIn() && (user.getId() == loggedInUser().getId());
        if( loggedInUser().getAdmin()){
            isOwner = true;
        }
        return  isOwner;
    }

    // if the user is logged in and is the profile owner allow edit
    public boolean canEditProfile(User profileUser){
        return isLoggedIn() && (profileUser.getId() == loggedInUser().getId());
    }

    // Authenticates the user via spring security
    public void authenticate(User user) {
        UserDetails userDetails = new UserWithRoles(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
    }

    // Set admin dashboard
    public Model setAdminDash(Model model){
        model.addAttribute("users",userDao.findAll());
        model.addAttribute("userModel",new User());
        model.addAttribute("ingredients",ingredientDao.findAll());
        model.addAttribute("ingredientModel",new Ingredient());
        model.addAttribute("categories",catDao.findAll());
        model.addAttribute("categoryModel",new Category());
        model.addAttribute("recipes",recipeDao.findAll());
        model.addAttribute("recipeModel",new Recipe());
        model.addAttribute("pantries",pantryDao.findAll());
        model.addAttribute("images",imageDao.findAll());
        model.addAttribute("pantryIngredients",pantryIngDao.findAll());
        model.addAttribute("recipeIngredients",recipeIngDao.findAll());
        return model;
    }

    // delete user

    @Transactional
    public void deleteUser(User user){
        for( Recipe recipe : user.getRecipes()){
            recipeServ.deleteRecipe(recipe);
        }
        favDao.deleteAllByUser(user);
        followDao.deleteAllByUser(user);
        followDao.deleteAllByFriend(user);
        panIngDao.deleteAllByPantry(user.getPantry());
        pantryDao.delete(user.getPantry());
        if(user.getAvatar() != null && user.getAvatar().startsWith("https://s3")){
            s3.deleteFile(user.getAvatar());
        }
        userDao.save(user);
        userDao.delete(user);
    }
}
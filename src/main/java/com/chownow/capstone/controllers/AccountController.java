package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {
	@Autowired
	private UserRepository userDao;

	@Autowired
	private FollowRepository followDao;

	@Autowired
	private RecipeIngredientRepository recipeIngredientDao;

	@Autowired
	private RecipeRepository recipeDao;

	@Autowired
	private PantryIngredientRepository pantryIngredientDao;

	@Autowired
	private PantryRepository pantryDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/sign-up")
	public String showSingnupForm(Model model){
		model.addAttribute("user", new User());
		return "/new";
	}

	@PostMapping("/sign-up")
	public String newUser(@ModelAttribute User user) {
		String hash = passwordEncoder.encode(user.getPassword());
		user.setPassword(hash);
		userDao.save(user);
		return "redirect:/";
	}

	@GetMapping("/users")
	public String showUsers(Model model) {
		model.addAttribute("users", userDao.findAll());
		return "users/index";
	}

	@GetMapping("/users/search")
	public String showUsersbySearch(Model model) {
 		model.addAttribute("followers", followDao.findAllByFollowee(userDao.getOne(1L)));
		return "users/index";
	}

	@GetMapping("users/{id}")
	public String showUserProfile(@PathVariable long id, Model model){
		/*Get user*/
		User user = userDao.getOne(id);
		model.addAttribute("user",user);
		/*Get all user recipes @recipes_table*/
		model.addAttribute("recipes", recipeDao.findAllByChef(user));
		/*Get Pantry*/
		model.addAttribute("pantry", pantryDao.findPantryByOwner(user));
		/*Get Pantry Ingredients*/
		/*4am trying to do some crazy method chaining is there a better way to declare what I need*/
		model.addAttribute("pantry", pantryIngredientDao.findPantryIngredientsByPantryId(pantryDao.findPantryByOwner(user)));
		return "/users/show";
	}

	@PostMapping("users/{id}/edit")
	public String editUser(@PathVariable long id, Model model){
		model.addAttribute("user",userDao.getOne(id));
		return "redirect:/users/show";
	}

	@PostMapping("/users/{id}/delete")
	public String deleteAd(@PathVariable long id){
		userDao.deleteById(id);
		return "redirect:/";
	}

	/*Todo: Roles*/
	/*	@PostMapping("/users/{id}/disable")
	public String disableAd(Long id) {
		User user = userDao.findUserById(id);
		user.disable();
		userDao.save(user);
		return "redirect:/users";
	}*/
}



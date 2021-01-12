package com.chownow.capstone.controllers;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.*;
import com.chownow.capstone.services.RecipeService;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {

	@Autowired
	private UserService userServ;

	@Autowired
	private UserRepository userDao;

	@Autowired
	private RecipeRepository recipeDao;

	@Autowired
	private PantryRepository pantryDao;

	@Autowired
	private CategoryRepository catDao;

	@Autowired
	private  IngredientRepository ingredientDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RecipeService recipeServ;

	@Autowired
	private PantryIngredientRepository pantryIngDao;

	@Autowired
	private RecipeIngredientRepository recipeIngDao;

	@GetMapping("/admin")
	public String getDashboard(Model model) {
		User currentUser = userServ.loggedInUser();
		if(currentUser == null || !currentUser.getAdmin()){
			return "/error/403";	
		}
		model.addAttribute("currentUser",currentUser);
		userServ.setAdminDash(model);
		return "users/admin/index";
	}

	@PostMapping("/admin/ingredients/new")
	public String createIngredient(@ModelAttribute Ingredient ingredient){
		ingredientDao.save(ingredient);
		return "redirect:/admin";
	}

	@GetMapping("/admin/ingredients/{id}/edit")
	public String getFormIngredient(@PathVariable long id,Model model){
		model.addAttribute("ingredientModel",ingredientDao.findById(id));
		return "users/admin/forms :: ingredientEdit";
	}

	@PostMapping("/admin/ingredients/edit")
	public String editIngredient(@RequestParam("name") String name, @RequestParam("id") long id){
		Ingredient i = ingredientDao.getOne(id);
		i.setName(name);
		ingredientDao.save(i);
		return "redirect:/admin";
	}

	@PostMapping("/admin/ingredients/{id}/delete")
	@Transactional
	public String deleteIngredient(@PathVariable long id){
		Ingredient ingredient = ingredientDao.getOne(id);
		pantryIngDao.deleteAllByIngredient(ingredient);
		recipeIngDao.deleteAllByIngredient(ingredient);
		ingredientDao.deleteById(id);
		return "redirect:/admin";
	}

	@PostMapping("/admin/categories/new")
	public String createCategory(@ModelAttribute Category category){
		catDao.save(category);
		return "redirect:/admin";
	}

	@GetMapping("/admin/categories/{id}/edit")
	public String getFormCategory(@PathVariable long id,Model model){
		model.addAttribute("categoryModel",catDao.findById(id));
		return "users/admin/forms :: categoryEdit";
	}

	@PostMapping("/admin/categories/edit")
	public String editCategory(@ModelAttribute Category category){
		catDao.save(category);
		return "redirect:/admin";
	}

	@PostMapping("/admin/categories/{id}/delete")
	@Transactional
	public String deleteCategory(@PathVariable long id){
		Category category = catDao.getOne(id);
		for(Recipe r : category.getRecipes()){
			r.getCategories().remove(category);
			recipeDao.save(r);
		}
		catDao.save(category);
		catDao.deleteById(id);
		return "redirect:/admin";
	}

	@PostMapping("/admin/users/new")
	public String createUser(@Valid @ModelAttribute User user,
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
			userServ.setAdminDash(model);
			model.addAttribute("errors", validation);
			model.addAttribute("userModel", user);
			return "users/admin/index";
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
		return "redirect:/admin";
	}

	@PostMapping("/admin/users/{id}/grant")
	public String makeAdmin(@PathVariable long id){
		User dbUser = userDao.getOne(id);
		if (dbUser.getAdmin()) {
			dbUser.setAdmin(false);
		} else {
			dbUser.setAdmin(true);
		}
		userDao.save(dbUser);
		return "redirect:/admin";
	}

	@PostMapping("/admin/users/{id}/delete")
	public String deleteUser(@PathVariable long id){
		User user = userDao.getOne(id);
		userServ.deleteUser(user);
		return "redirect:/admin";
	}

	@PostMapping("/admin/recipes/new")
	public String createRecipe( @Valid @ModelAttribute Recipe recipe,
								Errors validation,
								Model model
	) {
		if (validation.hasErrors()) {
			model.addAttribute("errors", validation);
			model.addAttribute("recipeModel", recipe);
			return "recipes/new";
		}
		recipeDao.save(recipe);
		return "redirect:/admin";
	}

	@PostMapping("/admin/recipes/{id}/delete")
	public String deleteRecipe(@PathVariable long id){
		recipeServ.deleteRecipe(recipeDao.getOne(id));
		return "redirect:/admin";
	}
}

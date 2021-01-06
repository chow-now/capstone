package com.chownow.capstone.controllers;

import com.chownow.capstone.models.Category;
import com.chownow.capstone.models.Ingredient;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.*;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
	private RecipeIngredientRepository recipeIngDao;

	@Autowired
	private PantryIngredientRepository pantryIngDao;

	@Autowired
	private  ImageRepository imageDao;

	@GetMapping("/admin")
	public String getDashboard(Model model) {
		User currentUser = userServ.loggedInUser();
		if(currentUser == null || !currentUser.getAdmin()){
			return "/error/403";	
		}
		model.addAttribute("currentUser",currentUser);
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
		return "users/admin/index";
	}

	@PostMapping("/admin/ingredients/new")
	public String createIngredient(@RequestBody Ingredient ingredient){
		ingredientDao.save(ingredient);
		return "users/admin/index";
	}

	@PostMapping("/admin/ingredients/edit")
	public String editIngredient(@RequestBody Ingredient ingredient){
		ingredientDao.save(ingredient);
		return "users/admin/index";
	}

	@PostMapping("/admin/ingredients/delete")
	public String deleteIngredient(@RequestBody Ingredient ingredient){
		ingredientDao.deleteById(ingredient.getId());
		return "users/admin/index";
	}

	@PostMapping("/admin/categories/new")
	public String createCategory(@RequestBody Category category){
		catDao.save(category);
		return "users/admin/index";
	}

	@PostMapping("/admin/categories/edit")
	public String editCategory(@RequestBody Category category){
		catDao.save(category);
		return "users/admin/index";
	}

	@PostMapping("/admin/categories/delete")
	public String deleteCategory(@RequestBody Category category){
		catDao.deleteById(category.getId());
		return "users/admin/index";
	}

	@PostMapping("/admin/users/new")
	public String createUser(@RequestBody User user){
		userDao.save(user);
		return "users/admin/index";
	}

	@PostMapping("/admin/users/access")
	public String makeAdmin(@RequestBody User user){
		User dbUser = userDao.getOne(user.getId());
		dbUser.setAdmin(true);
		userDao.save(user);
		return "users/admin/index";
	}

	@PostMapping("/admin/users/delete")
	public String deleteUser(@RequestBody User user){
		userDao.deleteById(user.getId());
		return "users/admin/index";
	}

	@PostMapping("/admin/recipes/new")
	public String createRecipe(@RequestBody Recipe recipe){
		recipeDao.save(recipe);
		return "users/admin/index";
	}

	@PostMapping("/admin/recipes/delete")
	public String deleteRecipe(@RequestBody Recipe recipe){
		recipeDao.deleteById(recipe.getId());
		return "users/admin/index";
	}
}

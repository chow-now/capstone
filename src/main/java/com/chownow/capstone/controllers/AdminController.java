package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.*;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

	@Autowired
	private UserService userServ;

	@Autowired
	private UserRepository userDao;

	@Autowired
	private FollowRepository followDao;

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
		model.addAttribute("members",userDao.findAllByIsAdminFalse());
		model.addAttribute("admins",userDao.findAllByIsAdminTrue());
		model.addAttribute("ingredients",ingredientDao.findAll());
		model.addAttribute("categories",catDao.findAll());
		model.addAttribute("recipes",recipeDao.findAll());
		model.addAttribute("pantries",pantryDao.findAll());
		model.addAttribute("images",imageDao.findAll());
		model.addAttribute("pantryIngredients",pantryIngDao.findAll());
		model.addAttribute("recipeIngredients",recipeIngDao.findAll());
		return "/users/admin/index";
	}
}

package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MasterController {

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

	@GetMapping("/master")
	public String showSingnupForm(Model model) {
		/*Find One User*/
		User user = userDao.getOne(12L);

		/*Pass in single user*/
		model.addAttribute("user",user);

		/*Get all user recipes @recipes_table*/
		model.addAttribute("recipes", recipeDao.findAllByChef(user));
		return "/master";
	}
}

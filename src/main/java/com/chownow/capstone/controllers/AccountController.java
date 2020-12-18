package com.chownow.capstone.controllers;

import com.chownow.capstone.models.Follow;
import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

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

//	@Autowired
//	private PasswordEncoder passwordEncoder;

	@GetMapping("/sign-up")
	public String showSingnupForm(Model model){
		model.addAttribute("user", new User());
		return "/signup";
	}

	@PostMapping("/sign-up")
	public String newUser(@ModelAttribute User user) {
		/*String hash = passwordEncoder.encode(user.getPassword());
		user.setPassword(hash);*/
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
		model.addAttribute("followers", userDao.findById(2L)); 		/*Todo: Hardcode*/

		return "users/index";
	}

	@GetMapping("user/{id}")
	public String getUserProfile(@PathVariable long id, Model model){
		/*Get user*/
		User user = userDao.getOne(id);
		model.addAttribute("user",user);
		/*Get all user recipes @recipes_table*/
		model.addAttribute("recipes", recipeDao.findAllByChef(user));
		/*Get Pantry*/
		return "/users/profile";
	}
	@GetMapping("users/{id}")
	public String showUserProfile(@PathVariable long id, Model model){
		/*Get user*/
		User currentUser = userDao.getOne(2L);
		User user = userDao.getOne(id);
		model.addAttribute("user",user);
		if(followDao.findByUserAndFriend(currentUser,user) != null){
			model.addAttribute("isFollowing",true);
		}
		/*Get all user recipes @recipes_table*/
		model.addAttribute("recipes", recipeDao.findAllByChef(user));
		/*Get Pantry*/
		return "users/profilev2";
	}


//	@PostMapping("/users/{id}/edit")
//	public String editRecipe(User userToBeSaved) {
//		User userDb = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		userToBeSaved.setUser(userDb); /*Todo: make setUser*/
//		userDao.save(userToBeSaved);
//		return "redirect:/user";
//	}

	@GetMapping("users/{id}/edit")
	public String showEditUser(@PathVariable long id, Model model){
		model.addAttribute("user",userDao.getOne(id));
		return "/users/edit";
	}

	@PostMapping("users/{id}/edit")
	public String editUser(@PathVariable long id, Model model){
		model.addAttribute("user",userDao.getOne(id));
		return "redirect:/users/edit";
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


	// delete follow
	@PostMapping("/{id}/delete")
	public ResponseEntity<Follow> deleteFollow(@PathVariable ("id") long followId){
		Optional<Follow> follow = followDao.findById(followId);
		if(follow.isPresent()) {
			Follow dbFollow = follow.get();
			followDao.delete(dbFollow);
		}
		return ResponseEntity.ok().build();
	}
}



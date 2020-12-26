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



package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
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

	/*
	@Autowired
	private PasswordEncoder passwordEncoder;
	*/

	@GetMapping("/signup")
	public String showSingupForm(Model model){

		model.addAttribute("user", new User());
		return "/users/signup";
	}

	@PostMapping("/signup")
	public String newUser(@ModelAttribute User user) {
		/*
		String hash = passwordEncoder.encode(user.getPassword());
		user.setPassword(hash);
		User user = new User(requestParams.get("email"),requestParams.get("username"),requestParams.get("password"));
		*/
		userDao.save(user);
		return "redirect:/login";
	}

	@GetMapping("users/{id}")
	public String showUserProfile(@PathVariable long id, Model model){
		User user = userDao.getOne(id);
		model.addAttribute("user",user);
		return "users/profile";
	}

	@GetMapping("users/{id}/edit")
	public String showEditUser(@PathVariable long id, Model model){
		model.addAttribute("user",userDao.getOne(id));
		return "users/editprofile";
	}

//  Todo: Am Idiot forgot how edit user worked
//	@PostMapping("/users/{id}/edit")
//	public String editPost(@PathVariable long id, @ModelAttribute User user) {
//		Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		if (obj == null || !(obj instanceof UserDetails)) {
//			return "redirect:/login";
//		}
//        User user = (User) obj;
//        user.setId(id);
//        user.setUser(user);
//        userDao.save(user);
//
//        return "redirect:/user/" + user.getId();
//    }

	@PostMapping("/users/{id}/delete")
	public String deleteUser(@PathVariable long id){
		userDao.deleteById(id);
		return "redirect:/login";
	}
}



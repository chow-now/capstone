package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userDao;

    @GetMapping("/create")
    public String createUser(Model model){
        model.addAttribute("user",new User());
        return "users/new";
    }

    @PostMapping("/create")
    public String submitUser(
                @Valid @ModelAttribute User user,
                Errors validation,
                Model model
    ) {
            if(validation.hasErrors()){
                model.addAttribute("errors",validation);
                model.addAttribute("user",user);
                return "recipes/new";
            }
            user.setIsAdmin(false);
            userDao.save(user);
            return "users/show";
    }
}

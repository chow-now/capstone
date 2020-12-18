package com.chownow.capstone.controllers;

import com.chownow.capstone.models.Follow;
import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.FollowRepository;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userDao;
    @Autowired
    private FollowRepository followDao;

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
            user.setAdmin(false);
            userDao.save(user);
            return "profilev2";
    }


    @RequestMapping(value = "/follow", method = RequestMethod.POST, headers="Content-Type=application/json")
    public @ResponseBody Follow post(@RequestBody long friendId) {
        Follow friend = new Follow(userDao.getById(7L), userDao.getById(friendId));
        Follow dbFollow = followDao.save(friend);
        if(dbFollow == null){
            return null;
        }
        return dbFollow;
    }
}

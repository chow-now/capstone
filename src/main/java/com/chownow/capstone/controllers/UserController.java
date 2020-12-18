package com.chownow.capstone.controllers;

import com.chownow.capstone.models.AjaxFollowRequest;
import com.chownow.capstone.models.Follow;
import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.FollowRepository;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


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
    public @ResponseBody Follow post(@RequestBody AjaxFollowRequest ajaxFollowRequest) {
        User currentUser = userDao.getById(2L);
        User friend = userDao.getById(ajaxFollowRequest.getFriendId());
        Follow dbFollow = null;
        if(followDao.findByUserAndFriend(currentUser,friend) == null){
            dbFollow = followDao.save(new Follow(currentUser,friend));
        }
        return dbFollow;
    }
}

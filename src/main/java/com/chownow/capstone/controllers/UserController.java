package com.chownow.capstone.controllers;

import com.chownow.capstone.models.User;
import com.chownow.capstone.repos.RecipeRepository;
import com.chownow.capstone.repos.UserRepository;
import com.chownow.capstone.services.AmazonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AmazonService s3;

    @Autowired
    private UserRepository userDao;

    @Autowired
    private RecipeRepository recipeDao;

    @GetMapping("/register")
    public String registerUser(){
        return "users/new";
    }

    @GetMapping("/{id}")
    public String getProfile(@PathVariable long id, Model model){
        User user = userDao.getOne(id);
        model.addAttribute("user",user);
        model.addAttribute("recipes", recipeDao.findAllByChef(user));
        return "users/show";
    }
    
    @PostMapping("/{id}/upload")
    public String uploadFile(@PathVariable long id, @RequestParam(value="file") MultipartFile multipartFile, Model model){
        User user = userDao.getOne(id);
        if(!user.getAvatar().isEmpty()){
            String path = user.getAvatar().split("codeup-s3/")[1];
            s3.deleteFile(path);
        }
        s3.uploadAvatar(multipartFile, user);
        model.addAttribute("user",user);
        return "users/show";
    }
}

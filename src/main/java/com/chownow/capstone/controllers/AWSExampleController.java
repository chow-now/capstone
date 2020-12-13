//package com.chownow.capstone.controllers;
//
//import com.chownow.capstone.models.Recipe;
//import com.chownow.capstone.models.User;
//import com.chownow.capstone.repos.RecipeRepository;
//import com.chownow.capstone.repos.UserRepository;
//import com.chownow.capstone.services.AmazonService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@Controller
//@RequestMapping("/aws")
//public class AWSExampleController {
//    @Autowired
//    private AmazonService s3;
//
//    @Autowired
//    private UserRepository userDao;
//
//    @Autowired
//    private RecipeRepository recipeDao;
//
//    @GetMapping("/avatar-form")
//    public String getAvatarForm(){
//        return "aws-example/aform";
//    }
//
//    @GetMapping("/recipe-form")
//    public String getRecipeForm(){
//        return "aws-example/rform";
//    }
//
//    @PostMapping("/users/{id}/upload")
//    public String uploadAvatar(@PathVariable long id, @RequestParam(value="file") MultipartFile multipartFile, Model model){
//        User user = userDao.getOne(id);
//        if(user.getAvatar() != null){
//            s3.deleteFile(user.getAvatar());
//        }
//        s3.uploadAvatar(multipartFile, user);
//        model.addAttribute("user",user);
//        return "aws-example/avatar";
//    }
//
//    @PostMapping("/recipes/{id}/upload")
//    public String uploadRecipeImage(@PathVariable long id, @RequestParam(value="file") MultipartFile multipartFile, Model model){
//        Recipe recipe = recipeDao.getOne(id);
//        s3.uploadRecipeImage(multipartFile, recipe);
//        model.addAttribute("recipe",recipe);
//        return "aws-example/recipe";
//    }
//
//    @PostMapping("/delete")
//    public void deleteFile(String imageUrl){
//        s3.deleteFile(imageUrl);
//    }
//
//}

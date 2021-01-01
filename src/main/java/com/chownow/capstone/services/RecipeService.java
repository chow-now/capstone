package com.chownow.capstone.services;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.RecipeRepository;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {
    @Autowired
    private static RecipeRepository recipeDao;
    @Autowired
    private static UserRepository userDoa;

    public static void main(String[] args) {

    }

}

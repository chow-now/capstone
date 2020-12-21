package com.chownow.capstone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomepage() {
        return "coming-soon";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        return "aboutus";
    }

}


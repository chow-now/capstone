package com.chownow.capstone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Controller
public class ApiTest {

    @GetMapping("/test")
    @ResponseBody
    public String testing() {
        Config config = new Config();
        HttpRequest request = HttpRequest.newBuilder()
                // GET Search
                .uri(URI.create("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?query=pasta&number=20&offset=0&type=main%20course"))
                .header("x-rapidapi-key", config.getKey())
                .header("x-rapidapi-host", config.getHost())
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
                HttpResponse<String> response = null;
        {
            try {
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response.body();
    }
}

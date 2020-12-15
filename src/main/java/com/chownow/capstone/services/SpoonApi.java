package com.chownow.capstone.services;

import com.chownow.capstone.models.Image;
import com.chownow.capstone.models.Recipe;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpoonApi {
    // GET Json from Http response
    public static JSONObject response(String uri) throws IOException, InterruptedException, ParseException {
        Config config = new Config();
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("x-rapidapi-key", config.getKey())
                .header("x-rapidapi-host", config.getHost())
                .build ();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("api response: "+response.body());

        // Parse String -> Obj
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(response.body());
    }

    public static List<Recipe> getRecipes(String uri) throws InterruptedException, ParseException, IOException {
        JSONObject object = response(uri);
        JSONArray array = (JSONArray) object.get("results");

        List<Recipe> list = new ArrayList<>();

//        System.out.println("array = " + array);

        for (Object obj : array) {
            System.out.println("obj = " + obj);
            JSONObject miniObject = (JSONObject) obj;
            //System.out.println("miniObject.get(\"id\") = " + miniObject.get("id"));
            Recipe recipe = new Recipe();
            recipe.setId(Long.parseLong(miniObject.get("id").toString()));
            recipe.setTitle((String) miniObject.get("title"));

            String img = (String)miniObject.get("imageUrl");
            Image image = new Image("https://spoonacular.com/recipeImages/"+img, recipe);
            //image.setId(() miniObject.get(recipe.getId()));

           list.add(recipe);
        }
        System.out.println("list = " + list);

        return list;
    }
}

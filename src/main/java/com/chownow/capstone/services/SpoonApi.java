package com.chownow.capstone.services;

import com.chownow.capstone.models.Recipe;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println(response.body());

        // Parse String -> Obj
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(response.body());
    }

    public static List<Recipe> getRecipes(String uri) throws InterruptedException, ParseException, IOException {
        JSONObject object = response(uri);
        JSONArray array = (JSONArray) object.get("results");

        List<Recipe> list = new ArrayList<>();
        for (Object obj : array) {
            JSONObject miniObject = (JSONObject) obj;
            Recipe recipe = new Recipe();
            recipe.setId((long) miniObject.get("id"));
            recipe.setTitle((String) miniObject.get("title"));

//            Image image = new Image("", recipe);
//            image.setId((long) miniObject.get(recipe.getId()));
//            image.setImage((String) miniObject.get());

            list.add(recipe);
        }
        System.out.println("list = " + list);

        return list;
    }
}

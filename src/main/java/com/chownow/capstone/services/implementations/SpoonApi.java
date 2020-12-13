package com.chownow.capstone.services.implementations;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SpoonApi {

    public static JSONObject response(String url) throws IOException, InterruptedException {
        Config config = new Config();
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = (HttpRequest) HttpRequest.newBuilder()
                .uri(URI.create(url + config.getKey()))
                .build ();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        // not returning parsed json object
        return (JSONObject) response.body();
    }
}

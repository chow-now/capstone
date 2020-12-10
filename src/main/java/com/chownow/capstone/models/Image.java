package com.chownow.capstone.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,length = 100)
    private String url;

    @ManyToMany(mappedBy = "images")
    private List<Recipe> recipes;


    public Image(){}

    public Image(long id, String url, List<Recipe> recipes) {
        this.id = id;
        this.url = url;
        this.recipes = recipes;
    }

    public Image(String url, List<Recipe> recipes) {
        this.url = url;
        this.recipes = recipes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}

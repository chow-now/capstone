package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@Table(name="images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Pattern(regexp = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|gif|png)",message = "Invalid file type")
    @Column(nullable = false,length = 100)
    private String url;

    @ManyToOne
    @JsonManagedReference
    private Recipe recipe;


    public Image(){}

    public Image(long id, String url, Recipe recipe) {
        this.id = id;
        this.url = url;
        this.recipe = recipe;
    }

    public Image(String url, Recipe recipe) {
        this.url = url;
        this.recipe = recipe;
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

    public Recipe getRecipes() {
        return recipe;
    }

    public void setRecipes(Recipe recipe) {
        this.recipe = recipe;
    }
}

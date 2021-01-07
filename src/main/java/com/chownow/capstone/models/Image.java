package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table(name="images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @Pattern(
//            regexp = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|gif|png)",
//            message = "INVALID FILE TYPE FOR RECIPE IMAGE"
//    )
    @Column(nullable = false,length = 100)
    private String url;

    @ManyToOne
    @JsonIgnore
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

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}

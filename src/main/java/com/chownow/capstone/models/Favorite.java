package com.chownow.capstone.models;

import javax.persistence.*;
@Entity
@Table(name="favorites")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;


    public Favorite(){}

    public Favorite(User user, Recipe recipe) {
        this.user = user;
        this.recipe = recipe;
    }

    public Favorite(long id, User user, Recipe recipe) {
        this.id = id;
        this.user = user;
        this.recipe = recipe;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


}

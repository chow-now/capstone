package com.chownow.capstone.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;

@Entity
@Table(name="recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,length = 100)
    private String title;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String directions;

    @Column(nullable = false,length = 100)
    private String difficulty;

    @Column(nullable = false)
    private int cook_time;

    @Column(nullable = false)
    private int prep_time;

    @Column(nullable = false)
    private int servings;

    @ManyToOne
    @JoinColumn(name = "cook_id")
    private User cook;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="recipe_ingredients",
            joinColumns={@JoinColumn(name="recipe_id")},
            inverseJoinColumns={@JoinColumn(name="ingredient_id")}
    )
    private List<Ingredient> ingredients;

    public Recipe(){}

    //setter
    public Recipe(String title, String directions, String difficulty, int cook_time, int prep_time, int servings, User cook) {
        this.title = title;
        this.directions = directions;
        this.difficulty = difficulty;
        this.cook_time = cook_time;
        this.prep_time = prep_time;
        this.servings = servings;
        this.cook = cook;
    }
    //getter
    public Recipe(long id, String title, String directions, String difficulty, int cook_time, int prep_time, int servings, User cook) {
        this.id = id;
        this.title = title;
        this.directions = directions;
        this.difficulty = difficulty;
        this.cook_time = cook_time;
        this.prep_time = prep_time;
        this.servings = servings;
        this.cook = cook;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getCook_time() {
        return cook_time;
    }

    public void setCook_time(int cook_time) {
        this.cook_time = cook_time;
    }

    public int getPrep_time() {
        return prep_time;
    }

    public void setPrep_time(int prep_time) {
        this.prep_time = prep_time;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public User getCook() {
        return cook;
    }

    public void setCook(User cook) {
        this.cook = cook;
    }
}

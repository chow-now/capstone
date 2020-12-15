package com.chownow.capstone.models;

import javax.persistence.*;
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
    private int cookTime;

    @Column(nullable = false)
    private int prepTime;

    @Column(nullable = false)
    private int servings;

    @ManyToOne
    @JoinColumn(name = "cook_id")
    private User chef;

    @OneToMany(mappedBy = "recipe")
    private List<Image> images;

    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RecipeIngredient> RecipeIngredients;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="recipe_categories",
            joinColumns={@JoinColumn(name="recipe_id")},
            inverseJoinColumns={@JoinColumn(name="category_id")}
    )
    private List<Category> categories;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="favorites",
            joinColumns={@JoinColumn(name="recipe_id")},
            inverseJoinColumns={@JoinColumn(name="user_id")}
    )
    private List<User> favoritedBy;

    public Recipe(){}

    //setter
    public Recipe(String title, String directions, String difficulty, int cookTime, int prepTime, int servings, User chef) {
        this.title = title;
        this.directions = directions;
        this.difficulty = difficulty;
        this.cookTime = cookTime;
        this.prepTime = prepTime;
        this.servings = servings;
        this.chef = chef;
    }
    //getter
    public Recipe(long id, String title, String directions, String difficulty, int cookTime, int prepTime, int servings, User chef) {
        this.id = id;
        this.title = title;
        this.directions = directions;
        this.difficulty = difficulty;
        this.cookTime = cookTime;
        this.prepTime = prepTime;
        this.servings = servings;
        this.chef = chef;
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

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public User getChef() {
        return chef;
    }

    public List<RecipeIngredient> getIngredients() {
        return RecipeIngredients;
    }

    public void setIngredients(List<RecipeIngredient> RecipeIngredients) {
        this.RecipeIngredients = RecipeIngredients;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<User> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(List<User> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }

    public void setChef(User chef) {
        this.chef = chef;
    }
}

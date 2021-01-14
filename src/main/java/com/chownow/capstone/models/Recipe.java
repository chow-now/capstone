package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;

@Entity
@Table(name="recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Spoon API ID
    @Column(nullable = true)
    private long spoonApiId;

    @Size(min = 0, message = "Title should be a bit longer.")
    @Size(max = 100,message = "Title is too long")
    @NotBlank(message= "Recipe needs a title")
    @Column(nullable = false,length = 100)
    private String title;

    @Size(min = 5, message = "Description should be a bit longer")
    @Size(max = 5000,message = "That's a bit long winded")
    @NotBlank(message= "Recipe needs a description")
    @Column(nullable = false,length = 5000)
    private String description;

    @Column
    private boolean isPublished;

    @Size(min = 20, message = "Directions should be a bit longer than 20 characters")
    @NotBlank(message= "Directions would be useful here. Please provides some.")
    @Column(columnDefinition = "TEXT")
    private String directions;


    @NotBlank(message = "Please set the level of difficulty.")
    @Column(nullable = false,length = 100)
    private String difficulty;

    @Min(0)
    @Max(999)
    @NotNull(message= "Oops. Looks like the cook time is missing.")
    @Column(nullable = false)
    private int cookTime;

    @Min(0)
    @Max(999)
    @NotNull(message= "Oops. Looks like the prep time is missing.")
    @Column(nullable = false)
    private int prepTime;

    @Min(1)
    @Max(value= 20, message = "Woah, that's a lot of servings!")
    @NotNull(message = "Did you forget the servings? Please provide an amount.")
    @Column(nullable = false)
    private int servings;

    @ManyToOne
    @JsonManagedReference(value="recipeRef")
    private User chef;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "recipe")
    private Set<Image> images = new HashSet<>();

    @OneToMany(mappedBy = "recipe")
    @JsonManagedReference
    private Set<RecipeIngredient> RecipeIngredients = new HashSet<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name="recipe_categories",
            joinColumns=@JoinColumn(name="recipe_id"),
            inverseJoinColumns=@JoinColumn(name="category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "recipe")
    private Set<Favorite> favoritedBy = new HashSet<>();

    public Recipe(){}

    //setter
    public Recipe(long spoonApiId, String title, String description, String directions, String difficulty, int cookTime, int prepTime, int servings, User chef) {
        this.spoonApiId = spoonApiId;
        this.title = title.trim();
        this.description = description.trim();
        this.directions = directions.trim();
        this.difficulty = difficulty;
        this.cookTime = cookTime;
        this.prepTime = prepTime;
        this.servings = servings;
        this.chef = chef;
    }
    //getter
    public Recipe(long id, long spoonApiId, String title, String description, String directions, String difficulty, int cookTime, int prepTime, int servings, User chef) {
        this.id = id;
        this.spoonApiId = spoonApiId;
        this.title = title;
        this.description = description;
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

    public long getSpoonApiId() {
        return spoonApiId;
    }

    public void setSpoonApiId(long spoonApiId) {
        this.spoonApiId = spoonApiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Set<Favorite> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(Set<Favorite> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }

    public Set<RecipeIngredient> getRecipeIngredients() {
        return RecipeIngredients;
    }

    public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        RecipeIngredients = recipeIngredients;
    }

    public void setChef(User chef) {
        this.chef = chef;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }


}
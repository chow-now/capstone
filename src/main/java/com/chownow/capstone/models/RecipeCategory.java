package com.chownow.capstone.models;

import com.chownow.capstone.models.Category;
import com.chownow.capstone.models.Recipe;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name="recipe_categories")
public class RecipeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Recipe recipe;

    @ManyToOne
// @JsonIgnore
    private Category category;

    public RecipeCategory() {}

    public RecipeCategory(long id) {
        this.id = id;
    }

    public RecipeCategory(long id, Recipe recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    public RecipeCategory(long id, Recipe recipe, Category category) {
        this.id = id;
        this.recipe = recipe;
        this.category = category;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
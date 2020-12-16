package com.chownow.capstone.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="recipe_ingredients")
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id")
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Ingredient ingredient;

    public RecipeIngredient(){}

    public RecipeIngredient(double amount, String unit, Recipe recipe, Ingredient ingredient) {
        this.amount = amount;
        this.unit = unit;
        this.recipe = recipe;
        this.ingredient = ingredient;
    }

    public RecipeIngredient(long id, double amount, String unit, Recipe recipe, Ingredient ingredient) {
        this.id = id;
        this.amount = amount;
        this.unit = unit;
        this.recipe = recipe;
        this.ingredient = ingredient;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}

package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

@Entity
@Table(name="pantry_ingredients")
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id")
public class PantryIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String unit;

    @ManyToOne
    @JsonIgnore
    private Pantry pantry;


    @ManyToOne
//    @JsonIgnore
    private Ingredient ingredient;

    public PantryIngredient(){}

    public PantryIngredient(double amount, String unit, Pantry pantry, Ingredient ingredient) {
        this.amount = amount;
        this.unit = unit;
        this.pantry = pantry;
        this.ingredient = ingredient;
    }

    public PantryIngredient(long id, double amount, String unit, Pantry pantry, Ingredient ingredient) {
        this.id = id;
        this.amount = amount;
        this.unit = unit;
        this.pantry = pantry;
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

    public Pantry getPantry() {
        return pantry;
    }

    public void setPantry(Pantry pantry) {
        this.pantry = pantry;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

}

package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="pantries")
public class Pantry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private User owner;

    @OneToMany(
            mappedBy = "pantry",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    
    private List<PantryIngredient> pantryIngredients;

    public Pantry(){}

    public Pantry(User owner){
        this.owner = owner;
    }

    public Pantry(long id, User owner) {
        this.id = id;
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<PantryIngredient> getPantryIngredients() {
        return pantryIngredients;
    }

    public void setPantryIngredients(List<PantryIngredient> pantryIngredients) {
        this.pantryIngredients = pantryIngredients;
    }
}

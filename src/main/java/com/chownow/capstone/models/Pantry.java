package com.chownow.capstone.models;

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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="pantry_ingredients",
            joinColumns={@JoinColumn(name="pantry_id")},
            inverseJoinColumns={@JoinColumn(name="ingredient_id")}
    )
    private List<Ingredient> ingredients;

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

    public User getOwner() {
        return owner;
    }
}

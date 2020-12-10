package com.chownow.capstone.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name can't be empty")
    private String name;

    @ManyToMany(mappedBy = "ingredients")
    private List<Pantry> pantries;

    @ManyToMany(mappedBy = "ingredients")
    private List<Recipe> recipes;


    public Ingredient(){}
    //setter
    public Ingredient(String name) {
        this.name = name;
    }
    //getter
    public Ingredient(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

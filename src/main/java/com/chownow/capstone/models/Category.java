package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message= "Please provide a name")
    @Size(min = 2, message = "Name should be a bit longer.")
    @Pattern(regexp = "^([^0-9]*)", message = "Name must not contain numbers")
    @Column(nullable = false,length = 100)
    private String name;

    @Column(length = 100)
    private String icon;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Recipe> recipes = new HashSet<>();

    public Category(){}

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public Category(long id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

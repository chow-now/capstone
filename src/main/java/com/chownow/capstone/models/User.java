package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100, unique = true)
    @Email(message = "Email can't be empty")
    private String email;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "First name can't be empty")
    private String first_name;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Last name can't be empty")
    private String last_name;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Password can't be empty")
    @JsonIgnore
    private String password;

    @Column(length = 250)
    private String avatar;

    @Column(columnDefinition = "TEXT")
    private String about_me;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean is_admin;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cook")
    private List<Recipe> recipes;

    public User(){}

    // Setter
    public User(String email, String first_name, String last_name, String password) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
    }
    // Getter
    public User(long id, String email, String first_name, String last_name, String password, String avatar, String about_me, Boolean is_admin) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.avatar = avatar;
        this.about_me = about_me;
        this.is_admin = is_admin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public Boolean getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
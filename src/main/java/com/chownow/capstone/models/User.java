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
    private String firstName;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Last name can't be empty")
    private String lastName;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Password can't be empty")
    @JsonIgnore
    private String password;

    @Column(length = 250)
    private String avatar;

    @Column(columnDefinition = "TEXT")
    private String aboutMe;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean isAdmin;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chef")
    private List<Recipe> recipes;

    @OneToMany(mappedBy = "followee")
    private List<Follow> followings;

    @ManyToMany(mappedBy = "favoritedBy")
    private List<Recipe> favorites;

    public User(){}

    // Setter
    public User(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
    // Getter
    public User(long id, String email, String firstName, String lastName, String password, String avatar, String aboutMe, Boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.avatar = avatar;
        this.aboutMe = aboutMe;
        this.isAdmin = isAdmin;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public List<Follow> getFollowings() {
        return followings;
    }

    public void setFollowings(List<Follow> followings) {
        this.followings = followings;
    }

    public List<Recipe> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Recipe> favorites) {
        this.favorites = favorites;
    }
}
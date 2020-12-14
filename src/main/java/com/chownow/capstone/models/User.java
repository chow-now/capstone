package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;



    @Email(message = "Email can't be empty")
    @Pattern(
            regexp = "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})*$",
            message = "Please provide a valid email address"
    )
    @Column(nullable = false, length = 100, unique = true)
    private String email;


    @NotBlank(message = "First name can't be empty")
    @Size(min = 2,message = "That name is too short")
    @Pattern(regexp = "[a-zA-Z]+[-_]*[a-zA-Z]+", message = "Name must not contain numbers")
    @Column(nullable = false, length = 20)
    private String firstName;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Last name can't be empty")
    @Size(min = 2,message = "That last name is too short")
    @Pattern(regexp = "[a-zA-Z]+[-_]*[a-zA-Z]+", message = "Last name must not contain numbers")
    private String lastName;


    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Should have 1 lowercase letter, 1 uppercase letter, 1 number, 1 special character, and be at least 8 characters long"
    )
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Password can't be empty")
    @JsonIgnore
    private String password;

    @Pattern(regexp = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|gif|png)",message = "Invalid file type")
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
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
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
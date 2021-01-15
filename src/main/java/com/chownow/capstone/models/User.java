package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name="users")

public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	@Size(min = 8,message = "Password must be at least 8 characters")
	@Pattern.List({
					@Pattern(regexp = "(?=.*[0-9]).+"),
					@Pattern(regexp = "(?=.*[a-z]).+"),
					@Pattern(regexp = "(?=.*[A-Z]).+"),
					@Pattern(regexp = "(?=.*[!@#\\$%\\^&\\*]).+"),
					@Pattern(regexp = "(?=\\S+$).+")
	})
	@JsonIgnore
	private String password;

    @Email(message = "Email can't be empty")
	@NotBlank(message = "Email can't be empty")
    @Pattern(
            regexp = "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})*$",
            message = "Invalid Password"
    )
    @Column(nullable = false, length = 100, unique = true)
    private String email;
    
    @NotBlank(message = "First name can't be empty")
    @Size(min = 2,message = "That name is too short")
    @Pattern(regexp = "^([^0-9]*)$", message = "Name must not contain numbers")
    @Column(nullable = false, length = 20)
    private String firstName;

//	@Pattern(regexp = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|gif|png)",message = "Invalid file type")
	@Column(length = 250)
	private String avatar;

	@Column(columnDefinition = "TEXT")
	private String aboutMe;

	@Column
	@JsonIgnore
	private Boolean isAdmin = false;

	@Column(columnDefinition = "integer default 0")
	@JsonIgnore
	private int suggestedCount;

	@Enumerated(EnumType.STRING)
	@Column(name= "auth_provider")
	private AuthenticationProvider authProvider;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdAt;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	private Date updatedAt;
    
    @OneToMany(mappedBy = "chef", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonBackReference("recipeRef")
    private List<Recipe> recipes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Follow> followings = new HashSet<>();

    @OneToMany(mappedBy = "friend")
    @JsonIgnore
    private Set<Follow> followers = new HashSet<>();


    @OneToMany(mappedBy = "user")
    private Set<Favorite> favorites = new HashSet<>();


	@OneToOne(mappedBy="owner")
	@JsonBackReference(value="pantryRef")
	private Pantry pantry;

	public User(){}


	// Update User
	public User(String email, String firstName) {
		this.email = email;
		this.firstName = firstName.trim();
	}
	// Setter
	public User(String email, String firstName, String password) {
		this.email = email;
		this.firstName = firstName.trim();
		this.password = password;
	}
	// Getter
	public User(long id, String email, String firstName, String password, String avatar, String aboutMe, Boolean isAdmin) {
		this.id = id;
		this.email = email;
		this.firstName = firstName;
		this.password = password;
		this.avatar = avatar;
		this.aboutMe = aboutMe;
		this.isAdmin = isAdmin;
	}

	// security
	public User(User copy) {
		id = copy.id; // This line is SUPER important! Many things won't work if it's absent
		email = copy.email;
		firstName = copy.firstName;
		password = copy.password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Boolean getAdmin() {
		return isAdmin;
	}

	public void setAdmin(Boolean admin) {
		isAdmin = admin;
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

	public Set<Follow> getFollowings() {
		return followings;
	}

	public void setFollowings(Set<Follow> followings) {
		this.followings = followings;
	}

	public Set<Follow> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<Follow> followers) {
		this.followers = followers;
	}

	public Set<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(Set<Favorite> favorites) {
		this.favorites = favorites;
	}

	public Pantry getPantry() {
		return pantry;
	}

	public void setPantry(Pantry pantry) {
		this.pantry = pantry;
	}

	public AuthenticationProvider getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(AuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}

	public int getSuggestedCount() {
		return suggestedCount;
	}

	public void setSuggestedCount(int suggestedCount) {
		this.suggestedCount = suggestedCount;
	}
}

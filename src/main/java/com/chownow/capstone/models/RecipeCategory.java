import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="recipe_categories")
public class RecipeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Recipe recipe;

    @ManyToOne
// @JsonIgnore
    private Category category;


    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
package com.chownow.capstone.repos;

import com.chownow.capstone.models.Ingredient;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    List<RecipeIngredient> findAllByRecipe(Recipe recipe);
    void deleteAllByRecipe(Recipe recipe);
    void deleteAllByIngredient(Ingredient ingredient);
}

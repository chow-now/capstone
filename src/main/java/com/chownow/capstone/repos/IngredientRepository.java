package com.chownow.capstone.repos;

import com.chownow.capstone.models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findAllByNameLike(String name);
    Ingredient getFirstById(long id);
}

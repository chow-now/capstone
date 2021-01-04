package com.chownow.capstone.repos;

import com.chownow.capstone.models.PantryIngredient;
import com.chownow.capstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PantryIngredientRepository extends JpaRepository<PantryIngredient, Long> {
    // Get the list of posts in order by ingredient name.
    List<PantryIngredient> findAllByPantry_OwnerOrderByIngredient(User user);
}

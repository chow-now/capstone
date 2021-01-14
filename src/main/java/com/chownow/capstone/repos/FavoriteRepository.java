package com.chownow.capstone.repos;

import com.chownow.capstone.models.Favorite;
import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Favorite findByUserAndAndRecipe(User user, Recipe recipe);
    List<Favorite> findAllByUser(User user);
    void deleteAllByUser(User user);
    void deleteAllByRecipe(Recipe recipe);
}

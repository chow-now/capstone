package com.chownow.capstone.repos;

import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe getFirstById(long id);
    List<Recipe> findAllByChef(User cook);
    List<Recipe> findAllByTitle(String term);
    List<Recipe> findAllByPrepTimeIsLessThanEqual(int time);
    List<Recipe> findAllByCookTimeIsLessThanEqual(int time);
}

package com.chownow.capstone.repos;

import com.chownow.capstone.models.Image;
import com.chownow.capstone.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findAllById(long id);
    void deleteAllByRecipe(Recipe recipe);
    List<Image> findAllByRecipe(Recipe recipe);

}

package com.chownow.capstone.repos;

import com.chownow.capstone.models.Image;
import com.chownow.capstone.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findAllById(long id);
    void deleteAllByRecipe(Recipe recipe);
}

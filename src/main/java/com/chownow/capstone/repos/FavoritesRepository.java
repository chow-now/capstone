package com.chownow.capstone.repos;

import com.chownow.capstone.models.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
}

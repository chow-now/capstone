package com.chownow.capstone.repos;

import com.chownow.capstone.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

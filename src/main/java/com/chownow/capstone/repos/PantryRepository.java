package com.chownow.capstone.repos;

import com.chownow.capstone.models.Pantry;
import com.chownow.capstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PantryRepository extends JpaRepository<Pantry, Long> {
    Pantry getFirstByOwner(User user);
}

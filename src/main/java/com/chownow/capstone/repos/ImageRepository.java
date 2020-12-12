package com.chownow.capstone.repos;

import com.chownow.capstone.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}

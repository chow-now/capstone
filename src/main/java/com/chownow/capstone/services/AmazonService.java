package com.chownow.capstone.services;

import com.chownow.capstone.models.Recipe;
import com.chownow.capstone.models.User;
import org.springframework.web.multipart.MultipartFile;

public interface AmazonService {
    String uploadAvatar(MultipartFile multipartFile, User user);
    String uploadRecipeImage(MultipartFile multipartFile, Recipe recipe);
    String uploadFile(MultipartFile multipartFile);
    void deleteFile(final String keyName);
}
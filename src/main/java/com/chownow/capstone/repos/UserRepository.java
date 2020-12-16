package com.chownow.capstone.repos;

import com.chownow.capstone.models.Follow;
import com.chownow.capstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIsAdminTrue();
    User getFirstByEmail(String email);
}

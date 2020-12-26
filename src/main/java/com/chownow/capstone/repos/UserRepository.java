package com.chownow.capstone.repos;


import com.chownow.capstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIsAdminTrue();
    List<User> findAllByIsAdminFalse();
    User getFirstByEmail(String email);
    User getById(Long id);
    User findByFirstName(String firstName);
    User getUserByEmail(String email);
    User getFirstById(long id);
}

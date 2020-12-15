package com.chownow.capstone.repos;

import com.chownow.capstone.models.Follow;
import com.chownow.capstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findAllByFollowee(User user);
    List<Follow> findAllByFollower(User user);
}

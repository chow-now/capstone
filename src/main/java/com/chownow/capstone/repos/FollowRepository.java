package com.chownow.capstone.repos;

import com.chownow.capstone.models.Follow;
import com.chownow.capstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByUserAndFriend(User user,User friend);
    List<Follow> findAllByFriend(User user);
    List<Follow> findAllByUser(User user);
}

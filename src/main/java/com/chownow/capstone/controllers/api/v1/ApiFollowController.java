package com.chownow.capstone.controllers.api.v1;

import com.chownow.capstone.models.Follow;
import com.chownow.capstone.repos.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/followings")
public class ApiFollowController {
    @Autowired
    private FollowRepository followDao;

    /* GET MAPPINGS */

    // get all users
    @GetMapping
    public @ResponseBody
    List<Follow> getAllFollows(){
        return followDao.findAll();
    }

    // get follow by id
    @GetMapping("/{id}")
    public @ResponseBody Follow getFollow(@PathVariable(value="id") long followId) {
        Optional<Follow> follow = followDao.findById(followId);
        return follow.orElse(null);
    }

    /* POST MAPPINGS FOR CRUD */

    // create follow
    @PostMapping("/new")
    public @ResponseBody
    Follow createFollow(@RequestBody Follow follow){
        return followDao.save(follow);
    }

    // update follow
    @PostMapping("/{id}/edit")
    public @ResponseBody Follow updateFollow(@RequestBody Follow requestFollow,@PathVariable (value = "id")long followId){
        Optional<Follow> follow = followDao.findById(followId);
        if(follow.isPresent()) {
            Follow dbFollow = follow.get();
            return followDao.save(dbFollow);
        }
        return null;
    }

    // delete follow
    @PostMapping("/{id}/delete")
    public ResponseEntity<Follow> deleteFollow(@PathVariable ("id") long followId){
        Optional<Follow> follow = followDao.findById(followId);
        if(follow.isPresent()) {
            Follow dbFollow = follow.get();
            followDao.delete(dbFollow);
        }
        return ResponseEntity.ok().build();
    }
}

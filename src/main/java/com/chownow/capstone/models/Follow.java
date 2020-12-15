package com.chownow.capstone.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name= "follows")
public class Follow {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @ManyToOne
        @JoinColumn(name = "follower_id")
        @JsonManagedReference
        private User follower;

        @ManyToOne
        @JoinColumn(name = "followee_id")
        @JsonManagedReference
        private User followee;

        public Follow(){}

        public Follow(User follower, User followee) {
                this.follower = follower;
                this.followee = followee;
        }

        public Follow(long id, User follower, User followee) {
                this.id = id;
                this.follower = follower;
                this.followee = followee;
        }

        public long getId() {
                return id;
        }

        public void setId(long id) {
                this.id = id;
        }

        public User getFollower() {
                return follower;
        }

        public void setFollower(User follower) {
                this.follower = follower;
        }

        public User getFollowee() {
                return followee;
        }

        public void setFollowee(User followee) {
                this.followee = followee;
        }


}

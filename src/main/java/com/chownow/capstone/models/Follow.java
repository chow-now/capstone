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
        @JoinColumn(name = "user_id")
        @JsonManagedReference
        private User user;

        @ManyToOne
        @JoinColumn(name = "friend_id")
        @JsonManagedReference
        private User friend;

        public Follow(){}

        public Follow(User user, User friend) {
                this.user = user;
                this.friend = friend;
        }

        public Follow(long id, User user, User friend) {
                this.id = id;
                this.user = user;
                this.friend = friend;
        }

        public long getId() {
                return id;
        }

        public void setId(long id) {
                this.id = id;
        }

        public User getUser() {
                return user;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public User getFriend() {
                return friend;
        }

        public void setFriend(User friend) {
                this.friend = friend;
        }
}

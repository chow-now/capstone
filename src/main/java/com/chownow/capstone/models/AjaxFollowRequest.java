package com.chownow.capstone.models;


public class AjaxFollowRequest {
    private long friendId;

    public AjaxFollowRequest(){}
    public AjaxFollowRequest(long friendId) {
        this.friendId = friendId;
    }

    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }
}

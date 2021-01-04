package com.chownow.capstone.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;

    public CustomOAuth2User(OAuth2User oauth2User){
        this.oauth2User = oauth2User;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public String getName() {
        return oauth2User.getAttributes().get("given_name").toString();
    }

    public String getFullName(){
        return oauth2User.getAttributes().get("name").toString();
    }

    public String getEmail(){
        return oauth2User.getAttributes().get("email").toString();
    }

    public String getAvatar(){
        System.out.println("USER AVATAR :" + oauth2User.getAttributes().get("picture"));
        return oauth2User.getAttributes().get("picture").toString();}
}
package com.chownow.capstone.security;

import com.chownow.capstone.models.*;
import com.chownow.capstone.repos.PantryRepository;
import com.chownow.capstone.repos.UserRepository;
import com.chownow.capstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

/* This handler is a hacky way of attaching the user db. It checks if the Oauth users email is in the db;
if the email is in db it updates the users information if not it creates the user in the db;
the user is then grabbed from the db and re-authenticated.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserRepository userDao;
    @Autowired
    private PantryRepository pantryDao;
    @Autowired
    private UserService userServ;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        String name = oAuth2User.getName();
        User dbUser = userDao.getUserByEmail(email);
        if(dbUser == null){
            System.out.println("REGISTERING USER");
            //register as new user
            User user = new User(email,name,randomPassword(30));
            user.setAuthProvider(AuthenticationProvider.GOOGLE);
            user.setAvatar(oAuth2User.getAvatar());
            user.setAdmin(false);
            User regUser = userDao.save(user);
            pantryDao.save(new Pantry(user));
            userServ.authenticate(regUser);
            System.out.println("USERS ID: "+regUser.getId());
            response.sendRedirect("/users/"+regUser.getId());
        }else{
            System.out.println("GETTING USER");
            // update existing customer
            if(!dbUser.getAuthProvider().toString().equalsIgnoreCase("google")){
                dbUser.setAuthProvider(AuthenticationProvider.GOOGLE);
                dbUser.setAvatar(oAuth2User.getAvatar());
                dbUser = userDao.save(dbUser);
            }
            userServ.authenticate(dbUser);
            response.sendRedirect("/users/"+dbUser.getId());
        }
    }

    // random auto hashed password to give the user when logged in with Oauth
    public static String randomPassword(int len) {
        System.out.println("IN AUTO HASH");
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                +"lmnopqrstuvwxyz!@#$%&0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString()+"Az09$";
    }
}

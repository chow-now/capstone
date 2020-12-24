package com.chownow.capstone.services;


import com.chownow.capstone.models.User;
import com.chownow.capstone.models.UserWithRoles;
import com.chownow.capstone.repos.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsLoader implements UserDetailsService {
    private final UserRepository usersDao;

    public UserDetailsLoader(UserRepository usersDao) {
        this.usersDao = usersDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = usersDao.getFirstByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found for " + email);
        }
//      Returns copy constructor of user
        return new UserWithRoles(user);
    }
}

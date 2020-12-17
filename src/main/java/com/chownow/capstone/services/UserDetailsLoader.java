//package com.chownow.capstone.services;
//
//import com.chownow.capstone.models.User;
//import com.chownow.capstone.models.UserWithRoles;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//public class UserDetailsLoader implements UserDetailsService {
//	private final User users;
//
//	public UserDetailsLoader(User users) {
//		this.users = users;
//	}
//
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		User user = users.findByUsername(username);
//		if (user == null) {
//			throw new UsernameNotFoundException("No user found for " + username);
//		}
//
//		return new UserWithRoles(user);
//	}
//}

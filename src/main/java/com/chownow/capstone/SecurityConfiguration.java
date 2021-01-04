package com.chownow.capstone;

import com.chownow.capstone.security.OAuth2LoginSuccessHandler;
import com.chownow.capstone.services.CustomOAuth2UserService;
import com.chownow.capstone.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsLoader usersLoader;
    @Autowired
    private CustomOAuth2UserService oAuth2UserService;
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usersLoader).passwordEncoder(passwordEncoder());
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                /* Pages that can be viewed without having to log in */
                .antMatchers(
                        "/",
                        "/recipes",
                        "/users/register",
                        "/oauth2/*"
                ).permitAll()
                /* Pages that require authentication */
                .antMatchers(
                        "/recipes/{id}/*",
                        "/recipes/{id}",
                        "/users/{id}",
                        "/dashboard",
                        "/users/{id}/*",
                        "/api"
                ).authenticated()
                /* Pages that require a role */
//                .antMatchers("/admin/*","/admin").hasRole("Admin")
                .and()
                /* Login configuration */
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard").permitAll()  //  all can access login page & on success redirect, it can be any URL
                /* OAuth2 login */
                .and()
                .oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint().userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2LoginSuccessHandler)
                /* Logout configuration */
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout") // append a query string value
                .and()
                /* remember me feature */
                .rememberMe()
                .tokenValiditySeconds(2592000) // sets expiration of token for remember me
        ;
    }
}
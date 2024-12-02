package com.rbac.config.security.web;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rbac.model.dao.UsersDao;
import com.rbac.model.entity.Users;
import com.rbac.util.AppUtil;

import lombok.extern.slf4j.Slf4j;

@Service("userDetailsService")
@Slf4j
public class AdminUserDetailsService implements UserDetailsService{

    private final UsersDao usersDao;

    private final PasswordEncoder encoder;

    @Autowired
    public AdminUserDetailsService(UsersDao usersDao, @Lazy PasswordEncoder encoder) {
        this.usersDao = usersDao;
        this.encoder = encoder;
    }

    /**
     * get UserDetails by username
     *
     * @param username
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username){
        log.info("Load users by username : {}", username);
        Optional<Users> optUser = usersDao.findByUsernameAndStatus(username, Users.UserStatus.ACTIVE);
        if (optUser.isEmpty()){
            throw new UsernameNotFoundException("User with the specified username " + username + " not found");
        }

        Users users = optUser.get();

        return User.withUsername(users.getUsername())
            .password(users.getPassword())
            .authorities(getUserAuthorities(users))
            .build();
    }

    public UserDetails loadUserByUsers(Users users) {
        return User.withUsername(users.getUsername())
                .password(users.getPassword())
                .authorities(getUserAuthorities(users))
                .build();
    }
    
    private Collection<GrantedAuthority> getUserAuthorities(Users users) {
        UserAuthorities rsAuthorities = users.getAuthorities();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        List<String> userAuthorities = Arrays.asList(rsAuthorities.name().split(","));
        if (!AppUtil.isNullOrEmptyString(userAuthorities)) {
            userAuthorities.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.toUpperCase().trim())));
        }
        return authorities;
    }
    
}

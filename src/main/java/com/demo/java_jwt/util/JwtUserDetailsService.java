package com.demo.java_jwt.util;

import com.demo.java_jwt.model.User;
import com.demo.java_jwt.service.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    AuthServiceImpl authService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        try {
            user = authService.getUserInfo(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<GrantedAuthority> listRole = new ArrayList<GrantedAuthority>();
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), listRole);
    }

}

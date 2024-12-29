package com.example.sunnymarketbackend.security;

import com.example.sunnymarketbackend.dao.UserDao;
import com.example.sunnymarketbackend.entity.Role;
import com.example.sunnymarketbackend.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDetailServerImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userDao.getUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found：" + email);
        }else{
            String userEmail = user.getEmail();
            String UserPassword = user.getPassword();

            // 放入權限
            List<Role> roleList = userDao.getRoleByUserId(user.getUserId());

            List<GrantedAuthority> authorities = convertToAuthorities(roleList);

            // 轉換成 Spring Security 指定的 User 格式
            return new User(userEmail, UserPassword, authorities);
        }
    }

    private List<GrantedAuthority> convertToAuthorities(List<Role> roleList) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for(Role role : roleList){
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }
}

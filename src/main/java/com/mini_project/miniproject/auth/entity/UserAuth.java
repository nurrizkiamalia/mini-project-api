//package com.mini_project.miniproject.auth.entity;
//
//import com.mini_project.miniproject.user.entity.Users;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//public class UserAuth extends Users implements UserDetails {
//    private final Users user;
//
//    public UserAuth(Users user) {
//        this.user = user;
//    }
//
//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return user.getEmail();
//    }
//
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(() -> "USER");
//        return authorities;
//    }
//
//}

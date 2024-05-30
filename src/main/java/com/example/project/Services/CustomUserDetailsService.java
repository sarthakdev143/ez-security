package com.example.project.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.project.Entity.User;
import com.example.project.Repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        System.out.println("Entered Load User By Username");
        
        Optional<User> user = userRepository.findByEmail(usernameOrEmail);
        System.out.println("Email : " + usernameOrEmail);   

        if (user.isPresent()) {
            System.out.println("Entered IF, Going to Next Method");
            return buildUserDetails(user.get());
        } else {
        System.out.println("Entered Else, Throwing Exception");
            throw new UsernameNotFoundException("Invalid email or password login failed");
        }
    }

    private UserDetails buildUserDetails(User user) {
        System.out.println("Build User Detail");
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        System.out.println("Returned User Detail");
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }
}
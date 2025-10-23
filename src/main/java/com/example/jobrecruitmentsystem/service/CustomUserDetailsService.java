package com.example.jobrecruitmentsystem.service;

import com.example.jobrecruitmentsystem.entity.User;
import com.example.jobrecruitmentsystem.repository.UserRepository;
import com.example.jobrecruitmentsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user); // ✅ Trả về custom user chứa ID
    }
}

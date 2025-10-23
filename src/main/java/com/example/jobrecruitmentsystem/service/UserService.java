package com.example.jobrecruitmentsystem.service;

import com.example.jobrecruitmentsystem.dto.UserRegisterDTO;
import com.example.jobrecruitmentsystem.entity.Role;
import com.example.jobrecruitmentsystem.entity.User;
import com.example.jobrecruitmentsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User registerUser(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Encode passwd
        user.setEmail(dto.getEmail());
        user.setFull_name(dto.getFull_name());
        user.setRole(dto.getRole());
        return userRepository.save(user);
    }
    public User updateUser(Long id, UserRegisterDTO dto, String requester) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getUsername().equals(requester) && !isAdmin(requester)) {
            throw new AccessDeniedException("You are not allowed to update this user");
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword())); // ✅ Thêm encode ở đây
        }
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFull_name(dto.getFull_name());
        user.setRole(dto.getRole());
        return userRepository.save(user);
    }


    public void deleteUser(Long id, String requester) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getUsername().equals(requester) && !isAdmin(requester)) {
            throw new AccessDeniedException("You are not allowed to delete this user");
        }
        userRepository.delete(user);
    }

    public boolean isAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getRole() == Role.ADMIN;
    }

}

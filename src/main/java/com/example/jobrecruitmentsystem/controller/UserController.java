package com.example.jobrecruitmentsystem.controller;

import com.example.jobrecruitmentsystem.dto.UserRegisterDTO;
import com.example.jobrecruitmentsystem.entity.User;
import com.example.jobrecruitmentsystem.repository.UserRepository;
import com.example.jobrecruitmentsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    private final UserService userService;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegisterDTO dto) {
        User user = userService.registerUser(dto);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPLICANT', 'COMPANY')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRegisterDTO dto, Authentication auth) {
        String requester = auth.getName();
        User updatedUser = userService.updateUser(id, dto, requester);
        return ResponseEntity.ok(updatedUser);
    }

    // 🗑️ Delete user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPLICANT', 'COMPANY')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication auth) {
        String requester = auth.getName();
        userService.deleteUser(id, requester);
        return ResponseEntity.ok().build();
    }
}


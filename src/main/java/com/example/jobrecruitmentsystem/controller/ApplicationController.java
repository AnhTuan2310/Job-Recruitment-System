package com.example.jobrecruitmentsystem.controller;

import com.example.jobrecruitmentsystem.dto.ApplicationDTO; // Thay đổi import
import com.example.jobrecruitmentsystem.entity.Application;
import com.example.jobrecruitmentsystem.entity.Job;
import com.example.jobrecruitmentsystem.entity.User;
import com.example.jobrecruitmentsystem.repository.UserRepository;
import com.example.jobrecruitmentsystem.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<Application> apply(@RequestBody ApplicationDTO dto, Authentication authentication) {
        boolean isAuthorized = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_COMPANY"));

        if (isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }// Đổi ApplicationRequest -> ApplicationDTO
        String username = authentication.getName();
        Application application = applicationService.apply(dto, username);
        return ResponseEntity.ok(application);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY')")
    public ResponseEntity<List<Application>> getAllApplications() {
        List<Application> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('APPLICANT') or hasRole('ADMIN')")
    public ResponseEntity<Application> updateApplication(@PathVariable Long id,
                                                         @RequestBody ApplicationDTO dto,
                                                         Authentication authentication) {
        boolean isAuthorized = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_COMPANY"));

        if (isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        String username = authentication.getName();
        Application updated = applicationService.updateApplication(id, dto, username);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('APPLICANT') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id,
                                               Authentication authentication) {
        boolean isAuthorized = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_COMPANY"));

        if (isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        String username = authentication.getName();
        applicationService.deleteApplication(id, username);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/my")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<List<Application>> getMyApplications(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Application> apps = applicationService.getByUserId(user.getId());
        return ResponseEntity.ok(apps);
    }
}
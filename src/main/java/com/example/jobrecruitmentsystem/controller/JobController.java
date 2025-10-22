package com.example.jobrecruitmentsystem.controller;

import com.example.jobrecruitmentsystem.dto.JobCreateDTO;
import com.example.jobrecruitmentsystem.entity.Application;
import com.example.jobrecruitmentsystem.entity.Job;
import com.example.jobrecruitmentsystem.entity.User;
import com.example.jobrecruitmentsystem.repository.ApplicationRepository;
import com.example.jobrecruitmentsystem.repository.JobRepository;
import com.example.jobrecruitmentsystem.repository.UserRepository;
import com.example.jobrecruitmentsystem.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_COMPANY', 'ROLE_ADMIN')")
    public ResponseEntity<Job> createJob(@RequestBody JobCreateDTO dto, Authentication authentication) {
        boolean isAuthorized = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_COMPANY"));

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        String username = authentication.getName();
        Job job = jobService.createJob(dto, username);
        authentication.getAuthorities().forEach(auth -> System.out.println(auth.getAuthority()));

        return ResponseEntity.ok(job);
    }


    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        // Allow anonymous access to view all jobs
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_COMPANY', 'ROLE_ADMIN')")
    public ResponseEntity<Job> updateJob(@PathVariable Long id,
                                         @RequestBody JobCreateDTO dto,
                                         Authentication authentication) {
        boolean isAuthorized = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_COMPANY"));

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        String username = authentication.getName();
        Job job = jobService.updateJob(id, dto, username);
        return ResponseEntity.ok(job);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_COMPANY', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id,
                                          Authentication authentication) {
        boolean isAuthorized = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")
                        || auth.getAuthority().equals("ROLE_COMPANY"));

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        String username = authentication.getName();
        jobService.deleteJob(id, username);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/createby/{id}")
    public ResponseEntity<List<Job>> getJobsByCreatorId(@PathVariable Long id) {
        List<Job> jobs = jobService.getJobsByCreatorId(id);
        return ResponseEntity.ok(jobs);
    }
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('COMPANY', 'ADMIN')")
    public ResponseEntity<List<Job>> getMyJobs(Authentication authentication) {
        String username = authentication.getName(); // lấy từ token
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();
        List<Job> jobs = jobRepository.findByCreatedBy_Id(userId);
        return ResponseEntity.ok(jobs);
    }
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getJobDetails(@PathVariable Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Application> applications = applicationRepository.findByJobId(id);

        List<Map<String, Object>> appDtos = applications.stream().map(app -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", app.getId());
            m.put("applicantName", app.getApplicant().getFull_name());
            m.put("coverLetter", app.getCoverLetter());
            m.put("status", app.getStatus());
            return m;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("id", job.getId());
        result.put("title", job.getTitle());
        result.put("description", job.getDescription());
        result.put("location", job.getLocation());
        result.put("salary", job.getSalary());
        result.put("status", job.getStatus());
        result.put("imageUrl", job.getImageUrl());
        result.put("createdAt", job.getCreatedAt());
        result.put("totalApplications", appDtos.size());
        result.put("applications", appDtos);

        return ResponseEntity.ok(result);
    }

}
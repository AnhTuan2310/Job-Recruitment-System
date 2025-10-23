package com.example.jobrecruitmentsystem.service;

import com.example.jobrecruitmentsystem.dto.ApplicationDTO;
import com.example.jobrecruitmentsystem.entity.*;
import com.example.jobrecruitmentsystem.repository.ApplicationRepository;
import com.example.jobrecruitmentsystem.repository.JobRepository;
import com.example.jobrecruitmentsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    public Application apply(ApplicationDTO dto, String applicantUsername) {
        System.out.println("USERNAME: " + applicantUsername);
        System.out.println("DTO JOB ID: " + dto.getJobId());
        User applicant = userRepository.findByUsername(applicantUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        System.out.println("APPLICANT ID: " + applicant);
        System.out.println("JOB ID: " + job);
        Application app = new Application();
        app.setJob(job);
        app.setApplicant(applicant);
        app.setCoverLetter(dto.getCoverLetter());
        app.setStatus(ApplicationStatus.PENDING);
        app.setCreatedAt(LocalDateTime.now());
        return applicationRepository.save(app);
    }


    // Lấy Job theo ID
    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    // Lấy User theo username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<Application> getApplicationsByJobId(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public Application save(Application app) {
        return applicationRepository.save(app);
    }
    public List<Application> getApplicationsByApplicant(String applicantUsername) {
        User applicant = userRepository.findByUsername(applicantUsername)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        return applicationRepository.findByApplicantId(applicant.getId());
    }
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }
    public Application updateApplication(Long id, ApplicationDTO dto, String username) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRole().equals(Role.ADMIN);
        boolean isCompany = user.getRole().equals(Role.COMPANY);
        boolean isOwner = app.getApplicant().getUsername().equals(username);

        if (!isAdmin && !isCompany && !isOwner) {
            throw new RuntimeException("Unauthorized to update this application");
        }

        // APPLICANT hoặc ADMIN được phép sửa cover letter
        if (dto.getCoverLetter() != null) {
            app.setCoverLetter(dto.getCoverLetter());
        }

        // Chỉ ADMIN hoặc COMPANY mới được sửa status
        if ((isAdmin || isCompany) && dto.getStatus() != null) {
            try {
                ApplicationStatus newStatus = ApplicationStatus.valueOf(dto.getStatus().toUpperCase());
                app.setStatus(ApplicationStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + dto.getStatus());
            }
        }

        return applicationRepository.save(app);
    }
    public void deleteApplication(Long id, String username) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals(Role.ADMIN) && !app.getApplicant().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to delete this application");
        }

        applicationRepository.delete(app);
    }
    public List<Application> getByUserId(Long userId) {
        return applicationRepository.findByApplicant_Id(userId);
    }
}
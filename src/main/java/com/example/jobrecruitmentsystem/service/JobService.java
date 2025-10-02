package com.example.jobrecruitmentsystem.service;

import com.example.jobrecruitmentsystem.dto.JobCreateDTO;
import com.example.jobrecruitmentsystem.entity.Job;
import com.example.jobrecruitmentsystem.entity.JobStatus;
import com.example.jobrecruitmentsystem.entity.User;
import com.example.jobrecruitmentsystem.repository.JobRepository;
import com.example.jobrecruitmentsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public Job createJob(JobCreateDTO dto, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setStatus(JobStatus.OPEN);
        job.setCreatedBy(creator);
        job.setCreatedAt(LocalDateTime.now());
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Job updateJob(Long id, JobCreateDTO dto, String username) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getCreatedBy().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to update this job");
        }

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setStatus(dto.getStatus());
        job.setImageUrl(dto.getImageUrl());

        return jobRepository.save(job);
    }

    public void deleteJob(Long id, String username) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getCreatedBy().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to delete this job");
        }

        jobRepository.delete(job);
    }

    public List<Job> getJobsByCreatorId(Long userId) {
        return jobRepository.findByCreatedBy_Id(userId);
    }
    public String saveImageForJob(Long jobId, MultipartFile imageFile) throws IOException {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        String uploadDir = "uploads/jobs";
        String fileName = imageFile.getOriginalFilename();
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) Files.createDirectories(path);

        Path filePath = path.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String imageUrl = "/uploads/jobs/" + fileName;
        job.setImageUrl(imageUrl);
        jobRepository.save(job);

        return imageUrl;
    }
    public List<Job> getJobsByCreator(Long userId) {
        return jobRepository.findByCreatedBy_Id(userId);
    }
}

package com.example.jobrecruitmentsystem.controller;

import com.example.jobrecruitmentsystem.config.FileStorageProperties;
import com.example.jobrecruitmentsystem.entity.Job;
import com.example.jobrecruitmentsystem.repository.JobRepository;
import com.example.jobrecruitmentsystem.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload-images")
@RequiredArgsConstructor
public class UploadController {
    private final JobService jobService;
    private final JobRepository jobRepository;
    private final FileStorageProperties properties;

    @PutMapping("/jobs/{id}")
    public ResponseEntity<String> uploadJobImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) throws IOException {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Tên file + đường dẫn lưu vật lý
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("src/main/resources/static/uploads/jobs");
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Đường dẫn URL public cho FE
        String imageUrl = "/uploads/jobs/" + filename;
        job.setImageUrl(imageUrl);
        jobRepository.save(job);

        return ResponseEntity.ok(imageUrl);
    }

}


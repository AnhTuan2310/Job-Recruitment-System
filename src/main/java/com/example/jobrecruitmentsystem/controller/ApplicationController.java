package com.example.jobrecruitmentsystem.controller;

import com.example.jobrecruitmentsystem.dto.ApplicationDTO; // Thay đổi import
import com.example.jobrecruitmentsystem.entity.*;
import com.example.jobrecruitmentsystem.repository.UserRepository;
import com.example.jobrecruitmentsystem.service.ApplicationService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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

@Value("${app.upload.dir}")
private String uploadDir;
    @PostMapping("/upload")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<Application> applyWithFile(
            @RequestParam("coverLetter") String coverLetter,
            @RequestParam("status") String status,
            @RequestParam("jobId") Long jobId,
            @RequestParam("cvFile") MultipartFile cvFile,
            Authentication authentication) {

        try {
            if (cvFile.isEmpty() || !cvFile.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().build();
            }

            String filename = UUID.randomUUID() + "_" + cvFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir); 
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(cvFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String username = authentication.getName();
            ApplicationDTO dto = new ApplicationDTO();
            dto.setJobId(jobId);
            dto.setCoverLetter(coverLetter);
            dto.setStatus(status);

            Application app = new Application();
            app.setJob(applicationService.getJobById(jobId)); // thêm phương thức getJobById() nếu chưa có
            app.setApplicant(applicationService.getUserByUsername(username)); // thêm phương thức getUserByUsername()
            app.setCoverLetter(coverLetter);
            app.setStatus(ApplicationStatus.valueOf(status.toUpperCase()));
            app.setCvFileName(filename); // ✅ set trước khi save
            applicationService.save(app);

            return ResponseEntity.ok(app);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<Application>> getApplicationsByJobId(@PathVariable Long jobId) {
        List<Application> applications = applicationService.getApplicationsByJobId(jobId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY')")
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        List<Application> applications = applicationService.getAllApplications();
        List<ApplicationDTO> dtos = applications.stream().map(app -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setId(app.getId());
            dto.setJobId(app.getJob().getId());
            dto.setJobTitle(app.getJob().getTitle()); // job title
            dto.setCoverLetter(app.getCoverLetter());
            dto.setStatus(app.getStatus().name());
            dto.setApplicantName(app.getApplicant().getUsername()); // tên người tạo
            dto.setCvUrl(app.getCvFileName() != null ? "/uploads/cv/" + app.getCvFileName() : null);
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
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
    @PreAuthorize("hasRole('APPLICANT') or hasRole('ADMIN') or hasRole('COMPANY')")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Application> apps = (user.getRole() == Role.ADMIN || user.getRole() == Role.COMPANY)
                ? applicationService.getAllApplications()
                : applicationService.getByUserId(user.getId());

        List<ApplicationDTO> dtos = apps.stream().map(app -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setId(app.getId());
            dto.setJobId(app.getJob().getId());
            dto.setJobTitle(app.getJob().getTitle());
            dto.setCoverLetter(app.getCoverLetter());
            dto.setStatus(app.getStatus().name());
            dto.setApplicantName(app.getApplicant().getUsername());
            dto.setCvUrl(app.getCvFileName() != null ? "/uploads/cv/" + app.getCvFileName() : null);
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/cv-files")
    @PreAuthorize("hasRole('APPLICANT') or hasRole('ADMIN') or hasRole('COMPANY')")
    public ResponseEntity<List<String>> getCvFiles() {
        try {
            Path cvPath = Paths.get("src/main/resources/static/uploads/cv");
            if (!Files.exists(cvPath)) {
                return ResponseEntity.ok(List.of());
            }
            
            List<String> files = Files.list(cvPath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.toLowerCase().endsWith(".pdf"))
                    .toList();
            
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cv/{fileName:.+}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY') or hasRole('APPLICANT')")
    public ResponseEntity<?> viewCvFile(@PathVariable String fileName, Authentication authentication) {
        try {
            String username = authentication.getName();
            Path filePath = Paths.get("src/main/resources/static/uploads/cv").resolve(fileName);

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File không tồn tại!");
            }

            // Kiểm tra quyền: applicant chỉ được xem CV của mình
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean isAdmin = user.getRole().name().equals("ADMIN");
            boolean isCompany = user.getRole().name().equals("COMPANY");

            // Nếu là admin hoặc công ty thì cho phép luôn
            if (isAdmin || isCompany) {
                byte[] fileBytes = Files.readAllBytes(filePath);
                return ResponseEntity.ok()
                        .header("Content-Type", "application/pdf")
                        .body(fileBytes);
            }

            // Nếu là applicant thì chỉ cho xem file của chính mình
            List<Application> myApps = applicationService.getByUserId(user.getId());
            boolean hasThisFile = myApps.stream()
                    .anyMatch(a -> fileName.equals(a.getCvFileName()));

            if (!hasThisFile) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Bạn không có quyền xem CV này!");
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .body(fileBytes);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc file CV!");
        }
    }
}
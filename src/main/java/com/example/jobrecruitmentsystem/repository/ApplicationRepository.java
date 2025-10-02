package com.example.jobrecruitmentsystem.repository;

import com.example.jobrecruitmentsystem.entity.Application;
import com.example.jobrecruitmentsystem.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    List<Application> findByApplicantId(Long applicantId);
    List<Application> findByStatus(ApplicationStatus status);
    List<Application> findByApplicant_Id(Long userId);
}
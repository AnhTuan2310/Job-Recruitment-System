package com.example.jobrecruitmentsystem.repository;

import com.example.jobrecruitmentsystem.entity.Job;
import com.example.jobrecruitmentsystem.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(JobStatus status);
    List<Job> findByCreatedBy_Id(Long userId);// ✅ Đúng cú pháp Spring Data JPA


}
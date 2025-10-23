package com.example.jobrecruitmentsystem.dto;

import com.example.jobrecruitmentsystem.entity.JobStatus;
import lombok.Data;

@Data
public class JobCreateDTO {
    private String title;
    private String description;
    private String location;
    private double salary;
    private JobStatus status;
    private String imageUrl; // optional nếu fen muốn cho phép tạo ảnh ngay
}


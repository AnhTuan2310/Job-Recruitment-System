package com.example.jobrecruitmentsystem.dto;

import com.example.jobrecruitmentsystem.entity.User;
import lombok.Data;

@Data
public class ApplicationDTO {
    private Long jobId;
    private Long id;
    private String coverLetter;
    private String status;
    private String jobTitle;
    private String applicantName;
    private String cvUrl;
}

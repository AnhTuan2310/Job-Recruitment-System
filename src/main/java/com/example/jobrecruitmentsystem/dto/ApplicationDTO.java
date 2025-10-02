package com.example.jobrecruitmentsystem.dto;

import com.example.jobrecruitmentsystem.entity.User;
import lombok.Data;

@Data
public class ApplicationDTO {
    private Long jobId;
    private String coverLetter;
    private String status;
}

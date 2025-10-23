package com.example.jobrecruitmentsystem.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class ApplicationRequest {
    @NotNull
    private Long jobId;
    private String coverLetter;
}

package com.example.jobrecruitmentsystem.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class ApplicationRequest {
    @NotNull
    private Long jobId;
    private String coverLetter;
}

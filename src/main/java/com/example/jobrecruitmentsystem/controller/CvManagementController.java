package com.example.jobrecruitmentsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CvManagementController {

    @GetMapping("/cv-management")
    public String cvManagementPage() {
        return "cvmanagement";
    }
}

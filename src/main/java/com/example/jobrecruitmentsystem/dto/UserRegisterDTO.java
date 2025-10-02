package com.example.jobrecruitmentsystem.dto;

import com.example.jobrecruitmentsystem.entity.Role;
import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String full_name;
    private Role role;
}

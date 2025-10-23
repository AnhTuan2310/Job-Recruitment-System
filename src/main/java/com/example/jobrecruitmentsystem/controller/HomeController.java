package com.example.jobrecruitmentsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // hiển thị file register.html trong templates
    }

    @GetMapping("/trangchu")
    public String trangchu() {
        return "trangchu";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/addjob")
    public String addjob() {
        return "addjob";
    }

    @GetMapping("/jobslist")
    public String jobslist() {
        return "jobslist";
    }

    @GetMapping("/myjobslist")
    public String myjobslist() {
        return "myjobslist";
    }

    @GetMapping("/apply")
    public String apply() {
        return "apply";
    }

    @GetMapping("/myapplylist")
    public String myapplylist() {
        return "myapplylist";
    }

    @GetMapping("/applylist")
    public String applylist() {
        return "applylist";
    }

    @GetMapping("/userlist")
    public String userlist() {
        return "userlist";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/addadmin")
    public String addadmin() {
        return "addadmin";
    }
}
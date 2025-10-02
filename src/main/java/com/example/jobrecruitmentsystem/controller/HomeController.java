package com.example.jobrecruitmentsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/reg")
    public String showRegisterPage() {
        return "register";
    }
    @GetMapping("/login")
    public String showLoginPage() {return "login";}
    @GetMapping("/dashboard")
    public String showDashBoardPage() {return "dashboard";}
    @GetMapping("/trangchu")
    public String showTrangchuPage() {return "trangchu";}
    @GetMapping("/addjob")
    public String showAddJobPage() {return "addjob";}
    @GetMapping("/apply")
    public String showApplyPage() {return "apply";}
    @GetMapping("/addadmin")
    public String showAddAdminPage() {return "addadmin";}
    @GetMapping("/jobslist")
    public String showJobslistPage() {return "jobslist";}
    @GetMapping("/applylist")
    public String showApplyListPage() {return "applylist";}
    @GetMapping("/userlist")
    public String showUserListPage() {return "userlist";}
    @GetMapping("/profile")
    public String showProfilePage() {return "profile";}
    @GetMapping("/myjobslist")
    public String showMyJobsListPage() {return "myjobslist";}
    @GetMapping("/myapplylist")
    public String showMyApplyListPage() {return "myapplylist";}
}

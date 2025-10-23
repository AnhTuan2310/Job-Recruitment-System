package com.example.jobrecruitmentsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String location;

    private double salary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(
            name = "created_by", // tên cột FK trong bảng job
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "JobFK_User")
    )
    private User createdBy;

    @Column(name="image_url")
    private String imageUrl;
}

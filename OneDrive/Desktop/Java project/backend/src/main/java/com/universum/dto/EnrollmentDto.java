package com.universum.dto;

import com.universum.entity.EnrollmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentUsername;
    private Long courseId;
    private String courseTitle;
    private String courseCode;
    private EnrollmentStatus status;
    private String note;
    private LocalDateTime enrolledAt;
}

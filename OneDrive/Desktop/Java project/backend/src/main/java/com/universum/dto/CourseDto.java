package com.universum.dto;

import com.universum.entity.CourseStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private String code;
    private Integer credits;
    private Integer maxStudents;
    private Integer enrolledCount;
    private CourseStatus status;
    private String professorName;
    private Long professorId;
    private LocalDateTime createdAt;
}

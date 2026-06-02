package com.universum.dto;

import com.universum.entity.CourseStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateCourseDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Course code is required")
    private String code;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits cannot exceed 10")
    private Integer credits;

    @NotNull(message = "Max students is required")
    @Min(value = 1, message = "At least 1 student must be allowed")
    private Integer maxStudents;

    private CourseStatus status;
}

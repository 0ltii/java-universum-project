package com.universum.mapper;

import com.universum.dto.EnrollmentDto;
import com.universum.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "studentUsername", source = "student.username")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "courseCode", source = "course.code")
    EnrollmentDto toDto(Enrollment enrollment);
}

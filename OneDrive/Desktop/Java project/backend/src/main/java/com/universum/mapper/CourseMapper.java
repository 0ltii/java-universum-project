package com.universum.mapper;

import com.universum.dto.CourseDto;
import com.universum.dto.CreateCourseDto;
import com.universum.entity.Course;
import com.universum.entity.EnrollmentStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "professorName", source = "professor.fullName")
    @Mapping(target = "professorId", source = "professor.id")
    @Mapping(target = "enrolledCount", expression = "java(countApprovedEnrollments(course))")
    CourseDto toDto(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "professor", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Course toEntity(CreateCourseDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "professor", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateCourseFromDto(CreateCourseDto dto, @MappingTarget Course course);

    default int countApprovedEnrollments(Course course) {
        if (course.getEnrollments() == null) return 0;
        return (int) course.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                .count();
    }
}

package com.universum.service;

import com.universum.dto.CourseDto;
import com.universum.dto.CreateCourseDto;
import com.universum.entity.Course;
import com.universum.entity.Role;
import com.universum.entity.User;
import com.universum.mapper.CourseMapper;
import com.universum.repository.CourseRepository;
import com.universum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toDto)
                .toList();
    }

    public List<CourseDto> getCoursesByProfessor(Long professorId) {
        return courseRepository.findByProfessorId(professorId).stream()
                .map(courseMapper::toDto)
                .toList();
    }

    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findByIdWithEnrollments(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));
        return courseMapper.toDto(course);
    }

    @Transactional
    public CourseDto createCourse(CreateCourseDto dto, String username) {
        if (courseRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Course code already exists: " + dto.getCode());
        }

        User professor = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Course course = courseMapper.toEntity(dto);
        course.setProfessor(professor);

        return courseMapper.toDto(courseRepository.save(course));
    }

    @Transactional
    public CourseDto updateCourse(Long id, CreateCourseDto dto, String username) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Role.ADMIN && !course.getProfessor().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only update your own courses");
        }

        if (!course.getCode().equals(dto.getCode()) && courseRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Course code already exists: " + dto.getCode());
        }

        courseMapper.updateCourseFromDto(dto, course);
        return courseMapper.toDto(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id, String username) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Role.ADMIN && !course.getProfessor().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only delete your own courses");
        }

        courseRepository.delete(course);
    }
}

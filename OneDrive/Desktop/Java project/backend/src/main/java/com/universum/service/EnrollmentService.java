package com.universum.service;

import com.universum.dto.EnrollmentDto;
import com.universum.entity.*;
import com.universum.mapper.EnrollmentMapper;
import com.universum.repository.CourseRepository;
import com.universum.repository.EnrollmentRepository;
import com.universum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;

    public List<EnrollmentDto> getMyEnrollments(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return enrollmentRepository.findByStudentId(student.getId()).stream()
                .map(enrollmentMapper::toDto)
                .toList();
    }

    public List<EnrollmentDto> getCourseEnrollments(Long courseId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (user.getRole() != Role.ADMIN && !course.getProfessor().getUsername().equals(username)) {
            throw new AccessDeniedException("Only the course professor or admin can view enrollments");
        }

        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(enrollmentMapper::toDto)
                .toList();
    }

    @Transactional
    public EnrollmentDto enroll(Long courseId, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Only students can enroll in courses");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (course.getStatus() != CourseStatus.OPEN) {
            throw new IllegalStateException("Course is not open for enrollment");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalStateException("You are already enrolled in this course");
        }

        long approvedCount = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.APPROVED);
        if (approvedCount >= course.getMaxStudents()) {
            throw new IllegalStateException("Course is full");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();

        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public EnrollmentDto updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus newStatus, String username) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isProfessorOfCourse = enrollment.getCourse().getProfessor().getUsername().equals(username);

        if (user.getRole() != Role.ADMIN && !isProfessorOfCourse) {
            throw new AccessDeniedException("Only the course professor or admin can update enrollment status");
        }

        enrollment.setStatus(newStatus);
        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public void withdrawFromCourse(Long courseId, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        enrollment.setStatus(EnrollmentStatus.WITHDRAWN);
        enrollmentRepository.save(enrollment);
    }
}

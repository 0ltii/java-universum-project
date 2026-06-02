package com.universum.controller;

import com.universum.dto.EnrollmentDto;
import com.universum.entity.EnrollmentStatus;
import com.universum.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentDto>> getMyEnrollments(Principal principal) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(principal.getName()));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentDto>> getCourseEnrollments(
            @PathVariable Long courseId,
            Principal principal) {
        return ResponseEntity.ok(enrollmentService.getCourseEnrollments(courseId, principal.getName()));
    }

    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentDto> enroll(@PathVariable Long courseId, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enroll(courseId, principal.getName()));
    }

    @PatchMapping("/{enrollmentId}/status")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<EnrollmentDto> updateStatus(
            @PathVariable Long enrollmentId,
            @RequestParam EnrollmentStatus status,
            Principal principal) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(enrollmentId, status, principal.getName()));
    }

    @DeleteMapping("/withdraw/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> withdraw(@PathVariable Long courseId, Principal principal) {
        enrollmentService.withdrawFromCourse(courseId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}

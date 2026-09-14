package com.smartedu.repository;

import com.smartedu.model.Assignment;
import com.smartedu.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseOrderByCreatedAtDesc(Course course);
    long countByCourse(Course course);
}

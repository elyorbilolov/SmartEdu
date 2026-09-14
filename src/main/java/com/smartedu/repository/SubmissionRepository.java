package com.smartedu.repository;

import com.smartedu.model.Assignment;
import com.smartedu.model.Course;
import com.smartedu.model.Submission;
import com.smartedu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, User student);
    List<Submission> findByAssignmentOrderBySubmittedAtDesc(Assignment assignment);
    List<Submission> findByStudentOrderBySubmittedAtDesc(User student);

    @Query("SELECT s FROM Submission s WHERE s.assignment.course = :course AND s.student = :student")
    List<Submission> findByCourseAndStudent(@Param("course") Course course, @Param("student") User student);

    @Query("SELECT s FROM Submission s WHERE s.assignment.course = :course")
    List<Submission> findByCourse(@Param("course") Course course);
}

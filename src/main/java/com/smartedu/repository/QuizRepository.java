package com.smartedu.repository;

import com.smartedu.model.Course;
import com.smartedu.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourseOrderByCreatedAtDesc(Course course);
    long countByCourse(Course course);
}

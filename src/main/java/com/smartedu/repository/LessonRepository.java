package com.smartedu.repository;

import com.smartedu.model.Course;
import com.smartedu.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseOrderByOrderIndexAsc(Course course);
    long countByCourse(Course course);
}

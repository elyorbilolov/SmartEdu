package com.smartedu.repository;

import com.smartedu.model.Course;
import com.smartedu.model.Lesson;
import com.smartedu.model.LessonProgress;
import com.smartedu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByStudentAndLesson(User student, Lesson lesson);
    List<LessonProgress> findByStudent(User student);

    @Query("SELECT lp FROM LessonProgress lp WHERE lp.student = :student AND lp.lesson.course = :course AND lp.completed = true")
    List<LessonProgress> findCompletedByStudentAndCourse(@Param("student") User student, @Param("course") Course course);

    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.student = :student AND lp.lesson.course = :course AND lp.completed = true")
    long countCompletedByStudentAndCourse(@Param("student") User student, @Param("course") Course course);
}

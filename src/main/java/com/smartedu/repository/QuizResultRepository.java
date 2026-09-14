package com.smartedu.repository;

import com.smartedu.model.Course;
import com.smartedu.model.Quiz;
import com.smartedu.model.QuizResult;
import com.smartedu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    Optional<QuizResult> findByQuizAndStudent(Quiz quiz, User student);
    List<QuizResult> findByQuizOrderByScoreDesc(Quiz quiz);
    List<QuizResult> findByStudentOrderByTakenAtDesc(User student);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.quiz.course = :course AND qr.student = :student")
    List<QuizResult> findByCourseAndStudent(@Param("course") Course course, @Param("student") User student);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.quiz.course = :course")
    List<QuizResult> findByCourse(@Param("course") Course course);
}

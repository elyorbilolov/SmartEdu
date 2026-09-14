package com.smartedu.repository;

import com.smartedu.model.Quiz;
import com.smartedu.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByQuizOrderByIdAsc(Quiz quiz);
}

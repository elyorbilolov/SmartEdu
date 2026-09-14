package com.smartedu.service;

import com.smartedu.model.*;
import com.smartedu.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizResultRepository quizResultRepository;

    public QuizService(QuizRepository quizRepository,
                       QuizQuestionRepository quizQuestionRepository,
                       QuizResultRepository quizResultRepository) {
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizResultRepository = quizResultRepository;
    }

    public List<Quiz> findQuizzesByCourse(Course course) {
        return quizRepository.findByCourseOrderByCreatedAtDesc(course);
    }

    public Optional<Quiz> findQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Transactional
    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    public List<QuizQuestion> findQuestionsByQuiz(Quiz quiz) {
        return quizQuestionRepository.findByQuizOrderByIdAsc(quiz);
    }

    @Transactional
    public QuizQuestion addQuestion(Quiz quiz, String questionText, String optA, String optB, String optC, String optD, String correct) {
        QuizQuestion question = new QuizQuestion(quiz, questionText, optA, optB, optC, optD, correct.toUpperCase().trim());
        return quizQuestionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        quizQuestionRepository.deleteById(questionId);
    }

    @Transactional
    public QuizResult evaluateQuiz(Long quizId, User student, Map<String, String> answers) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Test topilmadi: " + quizId));

        List<QuizQuestion> questions = quiz.getQuestions();
        int total = questions.size();
        int correct = 0;

        for (QuizQuestion q : questions) {
            String studentAnswer = answers.get("q_" + q.getId());
            if (studentAnswer != null && studentAnswer.trim().equalsIgnoreCase(q.getCorrectOption())) {
                correct++;
            }
        }

        // Check if student already took it, update or create
        QuizResult result = quizResultRepository.findByQuizAndStudent(quiz, student)
                .orElse(new QuizResult());

        result.setQuiz(quiz);
        result.setStudent(student);
        result.setTotalQuestions(total);
        result.setCorrectAnswers(correct);
        double percentage = total > 0 ? ((double) correct / total) * 100.0 : 0.0;
        result.setPercentage(percentage);
        result.setScore(Math.round(percentage * 10.0) / 10.0);
        result.setTakenAt(java.time.LocalDateTime.now());

        return quizResultRepository.save(result);
    }

    public Optional<QuizResult> findResultByQuizAndStudent(Quiz quiz, User student) {
        return quizResultRepository.findByQuizAndStudent(quiz, student);
    }

    public List<QuizResult> findResultsByQuiz(Quiz quiz) {
        return quizResultRepository.findByQuizOrderByScoreDesc(quiz);
    }

    public List<QuizResult> findResultsByStudent(User student) {
        return quizResultRepository.findByStudentOrderByTakenAtDesc(student);
    }

    public List<QuizResult> findResultsByCourseAndStudent(Course course, User student) {
        return quizResultRepository.findByCourseAndStudent(course, student);
    }

    public List<QuizResult> findResultsByCourse(Course course) {
        return quizResultRepository.findByCourse(course);
    }

    public long countAllQuizzes() {
        return quizRepository.count();
    }
}

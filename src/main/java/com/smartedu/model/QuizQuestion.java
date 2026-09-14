package com.smartedu.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @NotBlank(message = "Savol matni kiritilishi shart")
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @NotBlank(message = "A variant kiritilishi shart")
    private String optionA;

    @NotBlank(message = "B variant kiritilishi shart")
    private String optionB;

    @NotBlank(message = "C variant kiritilishi shart")
    private String optionC;

    @NotBlank(message = "D variant kiritilishi shart")
    private String optionD;

    @NotBlank(message = "To'g'ri variant belgilanishi shart")
    @Column(length = 2, nullable = false)
    private String correctOption; // "A", "B", "C", "D"

    public QuizQuestion() {
    }

    public QuizQuestion(Quiz quiz, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption) {
        this.quiz = quiz;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }
}

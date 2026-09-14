package com.smartedu.controller;

import com.smartedu.model.*;
import com.smartedu.service.CourseService;
import com.smartedu.service.QuizService;
import com.smartedu.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final CourseService courseService;
    private final UserService userService;
    private final QuizService quizService;

    public StudentController(CourseService courseService, UserService userService, QuizService quizService) {
        this.courseService = courseService;
        this.userService = userService;
        this.quizService = quizService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName()).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User student = getCurrentUser();
        List<Course> enrolledCourses = courseService.findCoursesByStudent(student);

        Map<Long, Integer> progressMap = new HashMap<>();
        for (Course c : enrolledCourses) {
            progressMap.put(c.getId(), courseService.getCourseProgressPercentage(student, c));
        }

        List<Submission> recentSubmissions = courseService.findSubmissionsByStudent(student);
        if (recentSubmissions.size() > 5) {
            recentSubmissions = recentSubmissions.subList(0, 5);
        }

        List<QuizResult> recentQuizResults = quizService.findResultsByStudent(student);
        if (recentQuizResults.size() > 5) {
            recentQuizResults = recentQuizResults.subList(0, 5);
        }

        model.addAttribute("student", student);
        model.addAttribute("courses", enrolledCourses);
        model.addAttribute("progressMap", progressMap);
        model.addAttribute("recentSubmissions", recentSubmissions);
        model.addAttribute("recentQuizResults", recentQuizResults);

        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String browseCourses(@RequestParam(value = "search", required = false) String search, Model model) {
        User student = getCurrentUser();
        List<Course> courses = courseService.searchCourses(search);
        List<Course> enrolledCourses = courseService.findCoursesByStudent(student);

        Set<Long> enrolledIds = new HashSet<>();
        for (Course c : enrolledCourses) {
            enrolledIds.add(c.getId());
        }

        model.addAttribute("courses", courses);
        model.addAttribute("enrolledIds", enrolledIds);
        model.addAttribute("search", search);
        return "student/courses";
    }

    @PostMapping("/courses/{id}/enroll")
    public String enrollInCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User student = getCurrentUser();
        boolean enrolled = courseService.enrollStudent(id, student);
        if (enrolled) {
            redirectAttributes.addFlashAttribute("successMessage", "Fanga muvaffaqiyatli a'zo bo'ldingiz!");
        } else {
            redirectAttributes.addFlashAttribute("infoMessage", "Siz ushbu fanga allaqachon a'zo bo'lgansiz.");
        }
        return "redirect:/student/courses/" + id;
    }

    @PostMapping("/courses/{id}/unenroll")
    public String unenrollCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User student = getCurrentUser();
        courseService.unenrollStudent(id, student);
        redirectAttributes.addFlashAttribute("successMessage", "Fandan chiqdingiz.");
        return "redirect:/student/dashboard";
    }

    @GetMapping("/courses/{id}")
    public String courseDetails(@PathVariable Long id, Model model) {
        User student = getCurrentUser();
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));

        boolean isEnrolled = courseService.isStudentEnrolled(course, student);

        model.addAttribute("course", course);
        model.addAttribute("isEnrolled", isEnrolled);
        model.addAttribute("lessons", courseService.findLessonsByCourse(course));
        model.addAttribute("assignments", courseService.findAssignmentsByCourse(course));
        model.addAttribute("quizzes", quizService.findQuizzesByCourse(course));
        model.addAttribute("announcements", courseService.findAnnouncementsByCourse(course));

        if (isEnrolled) {
            model.addAttribute("progress", courseService.getCourseProgressPercentage(student, course));
        }

        return "student/course-detail";
    }

    @GetMapping("/courses/{courseId}/lessons/{lessonId}")
    public String viewLesson(@PathVariable Long courseId, @PathVariable Long lessonId, Model model) {
        User student = getCurrentUser();
        Course course = courseService.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + courseId));
        Lesson lesson = courseService.findLessonById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Dars topilmadi: " + lessonId));

        boolean completed = courseService.isLessonCompleted(student, lesson);

        model.addAttribute("course", course);
        model.addAttribute("lesson", lesson);
        model.addAttribute("materials", lesson.getMaterials());
        model.addAttribute("completed", completed);
        model.addAttribute("allLessons", courseService.findLessonsByCourse(course));

        return "student/lesson-view";
    }

    @PostMapping("/lessons/{lessonId}/toggle-progress")
    public String toggleProgress(@PathVariable Long lessonId, @RequestParam("courseId") Long courseId) {
        User student = getCurrentUser();
        Lesson lesson = courseService.findLessonById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Dars topilmadi: " + lessonId));

        courseService.toggleLessonProgress(student, lesson);
        return "redirect:/student/courses/" + courseId + "/lessons/" + lessonId;
    }

    // Assignments
    @GetMapping("/assignments/{id}")
    public String viewAssignment(@PathVariable Long id, Model model) {
        User student = getCurrentUser();
        Assignment assignment = courseService.findAssignmentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topshiriq topilmadi: " + id));

        Submission submission = courseService.findSubmissionByAssignmentAndStudent(assignment, student).orElse(null);

        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", submission);
        return "student/assignment-view";
    }

    @PostMapping("/assignments/{id}/submit")
    public String submitAssignment(@PathVariable Long id,
                                   @RequestParam(value = "submittedText", required = false) String submittedText,
                                   @RequestParam(value = "file", required = false) MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        User student = getCurrentUser();
        Assignment assignment = courseService.findAssignmentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topshiriq topilmadi: " + id));

        try {
            courseService.submitAssignment(assignment, student, submittedText, file);
            redirectAttributes.addFlashAttribute("successMessage", "Yechimingiz muvaffaqiyatli topshirildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Topshirishda xatolik: " + e.getMessage());
        }

        return "redirect:/student/assignments/" + id;
    }

    // Quizzes
    @GetMapping("/quizzes/{id}/take")
    public String takeQuiz(@PathVariable Long id, Model model) {
        User student = getCurrentUser();
        Quiz quiz = quizService.findQuizById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test topilmadi: " + id));

        QuizResult prevResult = quizService.findResultByQuizAndStudent(quiz, student).orElse(null);

        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quiz.getQuestions());
        model.addAttribute("prevResult", prevResult);

        return "student/quiz-take";
    }

    @PostMapping("/quizzes/{id}/submit")
    public String submitQuiz(@PathVariable Long id,
                             @RequestParam Map<String, String> allParams,
                             RedirectAttributes redirectAttributes) {
        User student = getCurrentUser();
        QuizResult result = quizService.evaluateQuiz(id, student, allParams);

        redirectAttributes.addFlashAttribute("successMessage",
                String.format("Test yakunlandi! Natijangiz: %d/%d (%.1f%%)",
                        result.getCorrectAnswers(), result.getTotalQuestions(), result.getPercentage()));

        return "redirect:/student/quizzes/" + id + "/take";
    }

    // Grades
    @GetMapping("/grades")
    public String myGrades(Model model) {
        User student = getCurrentUser();
        List<Submission> submissions = courseService.findSubmissionsByStudent(student);
        List<QuizResult> quizResults = quizService.findResultsByStudent(student);
        List<Course> enrolledCourses = courseService.findCoursesByStudent(student);

        model.addAttribute("student", student);
        model.addAttribute("submissions", submissions);
        model.addAttribute("quizResults", quizResults);
        model.addAttribute("courses", enrolledCourses);

        return "student/grades";
    }
}

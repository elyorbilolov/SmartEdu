package com.smartedu.controller;

import com.smartedu.model.*;
import com.smartedu.service.CourseService;
import com.smartedu.service.QuizService;
import com.smartedu.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final CourseService courseService;
    private final UserService userService;
    private final QuizService quizService;

    public TeacherController(CourseService courseService, UserService userService, QuizService quizService) {
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
        User teacher = getCurrentUser();
        List<Course> courses = courseService.findCoursesByTeacher(teacher);
        model.addAttribute("courses", courses);
        model.addAttribute("teacher", teacher);

        int totalAssignments = 0;
        int totalSubmissions = 0;
        for (Course c : courses) {
            totalAssignments += c.getAssignments().size();
            for (Assignment a : c.getAssignments()) {
                totalSubmissions += a.getSubmissions().size();
            }
        }
        model.addAttribute("totalAssignments", totalAssignments);
        model.addAttribute("totalSubmissions", totalSubmissions);

        return "teacher/dashboard";
    }

    @GetMapping("/courses/{id}")
    public String courseDetails(@PathVariable Long id, Model model) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));

        model.addAttribute("course", course);
        model.addAttribute("lessons", courseService.findLessonsByCourse(course));
        model.addAttribute("assignments", courseService.findAssignmentsByCourse(course));
        model.addAttribute("quizzes", quizService.findQuizzesByCourse(course));
        model.addAttribute("announcements", courseService.findAnnouncementsByCourse(course));
        model.addAttribute("students", course.getStudents());

        return "teacher/course-detail";
    }

    // Lessons
    @GetMapping("/courses/{id}/lessons/new")
    public String newLessonForm(@PathVariable Long id, Model model) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));
        model.addAttribute("course", course);
        return "teacher/lesson-form";
    }

    @PostMapping("/courses/{id}/lessons/new")
    public String createLesson(@PathVariable Long id,
                               @RequestParam("title") String title,
                               @RequestParam(value = "orderIndex", defaultValue = "1") int orderIndex,
                               @RequestParam(value = "content", required = false) String content,
                               @RequestParam(value = "videoUrl", required = false) String videoUrl,
                               RedirectAttributes redirectAttributes) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));

        Lesson lesson = new Lesson(course, title, orderIndex, content, videoUrl);
        courseService.saveLesson(lesson);
        redirectAttributes.addFlashAttribute("successMessage", "Dars mavzusi muvaffaqiyatli qo'shildi!");
        return "redirect:/teacher/courses/" + id;
    }

    @GetMapping("/courses/{courseId}/lessons/{lessonId}/edit")
    public String editLessonForm(@PathVariable Long courseId, @PathVariable Long lessonId, Model model) {
        Course course = courseService.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + courseId));
        Lesson lesson = courseService.findLessonById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Dars topilmadi: " + lessonId));

        model.addAttribute("course", course);
        model.addAttribute("lesson", lesson);
        return "teacher/lesson-edit";
    }

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/edit")
    public String updateLesson(@PathVariable Long courseId,
                               @PathVariable Long lessonId,
                               @RequestParam("title") String title,
                               @RequestParam("orderIndex") int orderIndex,
                               @RequestParam(value = "content", required = false) String content,
                               @RequestParam(value = "videoUrl", required = false) String videoUrl,
                               RedirectAttributes redirectAttributes) {
        Lesson lesson = courseService.findLessonById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Dars topilmadi: " + lessonId));

        lesson.setTitle(title);
        lesson.setOrderIndex(orderIndex);
        lesson.setContent(content);
        lesson.setVideoUrl(videoUrl);
        courseService.saveLesson(lesson);

        redirectAttributes.addFlashAttribute("successMessage", "Dars muvaffaqiyatli yangilandi!");
        return "redirect:/teacher/courses/" + courseId;
    }

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/delete")
    public String deleteLesson(@PathVariable Long courseId, @PathVariable Long lessonId, RedirectAttributes redirectAttributes) {
        courseService.deleteLesson(lessonId);
        redirectAttributes.addFlashAttribute("successMessage", "Dars o'chirildi!");
        return "redirect:/teacher/courses/" + courseId;
    }

    // Materials
    @PostMapping("/lessons/{lessonId}/materials/new")
    public String addMaterial(@PathVariable Long lessonId,
                              @RequestParam("courseId") Long courseId,
                              @RequestParam("title") String title,
                              @RequestParam(value = "materialType", defaultValue = "file") String materialType,
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              @RequestParam(value = "externalUrl", required = false) String externalUrl,
                              RedirectAttributes redirectAttributes) {
        Lesson lesson = courseService.findLessonById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Dars topilmadi: " + lessonId));

        try {
            if ("file".equalsIgnoreCase(materialType) && file != null && !file.isEmpty()) {
                courseService.addMaterialWithFile(lesson, title, file);
                redirectAttributes.addFlashAttribute("successMessage", "Fayl muvaffaqiyatli yuklandi!");
            } else if (externalUrl != null && !externalUrl.isBlank()) {
                courseService.addMaterialWithLink(lesson, title, externalUrl);
                redirectAttributes.addFlashAttribute("successMessage", "Havola qo'shildi!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Material yuklashda xatolik: " + e.getMessage());
        }

        return "redirect:/teacher/courses/" + courseId;
    }

    @PostMapping("/materials/{id}/delete")
    public String deleteMaterial(@PathVariable Long id, @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        courseService.deleteMaterial(id);
        redirectAttributes.addFlashAttribute("successMessage", "Material o'chirildi!");
        return "redirect:/teacher/courses/" + courseId;
    }

    // Assignments
    @GetMapping("/courses/{id}/assignments/new")
    public String newAssignmentForm(@PathVariable Long id, Model model) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));
        model.addAttribute("course", course);
        return "teacher/assignment-form";
    }

    @PostMapping("/courses/{id}/assignments/new")
    public String createAssignment(@PathVariable Long id,
                                   @RequestParam("title") String title,
                                   @RequestParam(value = "description", required = false) String description,
                                   @RequestParam(value = "deadline", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline,
                                   @RequestParam(value = "maxScore", defaultValue = "100") int maxScore,
                                   @RequestParam(value = "file", required = false) MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));

        Assignment assignment = new Assignment(course, title, description, deadline, maxScore);
        courseService.saveAssignment(assignment, file);

        redirectAttributes.addFlashAttribute("successMessage", "Topshiriq muvaffaqiyatli yaratildi!");
        return "redirect:/teacher/courses/" + id;
    }

    @PostMapping("/assignments/{id}/delete")
    public String deleteAssignment(@PathVariable Long id, @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        courseService.deleteAssignment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Topshiriq o'chirildi!");
        return "redirect:/teacher/courses/" + courseId;
    }

    @GetMapping("/assignments/{id}/submissions")
    public String viewSubmissions(@PathVariable Long id, Model model) {
        Assignment assignment = courseService.findAssignmentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topshiriq topilmadi: " + id));

        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", courseService.findSubmissionsByAssignment(assignment));
        return "teacher/submissions";
    }

    @PostMapping("/submissions/{id}/grade")
    public String gradeSubmission(@PathVariable Long id,
                                  @RequestParam("score") int score,
                                  @RequestParam(value = "feedback", required = false) String feedback,
                                  RedirectAttributes redirectAttributes) {
        User teacher = getCurrentUser();
        Submission submission = courseService.gradeSubmission(id, score, feedback, teacher);
        redirectAttributes.addFlashAttribute("successMessage", "Baholash muvaffaqiyatli saqlandi!");
        return "redirect:/teacher/assignments/" + submission.getAssignment().getId() + "/submissions";
    }

    // Quizzes
    @GetMapping("/courses/{id}/quizzes/new")
    public String newQuizForm(@PathVariable Long id, Model model) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));
        model.addAttribute("course", course);
        return "teacher/quiz-form";
    }

    @PostMapping("/courses/{id}/quizzes/new")
    public String createQuiz(@PathVariable Long id,
                             @RequestParam("title") String title,
                             @RequestParam(value = "description", required = false) String description,
                             @RequestParam(value = "timeLimitMinutes", defaultValue = "15") int timeLimitMinutes,
                             RedirectAttributes redirectAttributes) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));

        Quiz quiz = new Quiz(course, title, description, timeLimitMinutes);
        quizService.saveQuiz(quiz);

        redirectAttributes.addFlashAttribute("successMessage", "Test yaratildi! Endi savollarni qo'shing.");
        return "redirect:/teacher/quizzes/" + quiz.getId() + "/questions";
    }

    @GetMapping("/quizzes/{id}/questions")
    public String quizQuestions(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.findQuizById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test topilmadi: " + id));

        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quizService.findQuestionsByQuiz(quiz));
        return "teacher/quiz-questions";
    }

    @PostMapping("/quizzes/{id}/questions/new")
    public String addQuizQuestion(@PathVariable Long id,
                                  @RequestParam("questionText") String questionText,
                                  @RequestParam("optionA") String optA,
                                  @RequestParam("optionB") String optB,
                                  @RequestParam("optionC") String optC,
                                  @RequestParam("optionD") String optD,
                                  @RequestParam("correctOption") String correctOption,
                                  RedirectAttributes redirectAttributes) {
        Quiz quiz = quizService.findQuizById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test topilmadi: " + id));

        quizService.addQuestion(quiz, questionText, optA, optB, optC, optD, correctOption);
        redirectAttributes.addFlashAttribute("successMessage", "Savol qo'shildi!");
        return "redirect:/teacher/quizzes/" + id + "/questions";
    }

    @PostMapping("/quizzes/questions/{id}/delete")
    public String deleteQuizQuestion(@PathVariable Long id, @RequestParam("quizId") Long quizId, RedirectAttributes redirectAttributes) {
        quizService.deleteQuestion(id);
        redirectAttributes.addFlashAttribute("successMessage", "Savol o'chirildi!");
        return "redirect:/teacher/quizzes/" + quizId + "/questions";
    }

    @GetMapping("/quizzes/{id}/results")
    public String viewQuizResults(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.findQuizById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test topilmadi: " + id));

        model.addAttribute("quiz", quiz);
        model.addAttribute("results", quizService.findResultsByQuiz(quiz));
        return "teacher/quiz-results";
    }

    @PostMapping("/quizzes/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        quizService.deleteQuiz(id);
        redirectAttributes.addFlashAttribute("successMessage", "Test o'chirildi!");
        return "redirect:/teacher/courses/" + courseId;
    }

    // Announcements
    @PostMapping("/courses/{id}/announcements/new")
    public String createAnnouncement(@PathVariable Long id,
                                     @RequestParam("title") String title,
                                     @RequestParam("content") String content,
                                     RedirectAttributes redirectAttributes) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));
        User teacher = getCurrentUser();

        Announcement announcement = new Announcement(course, teacher, title, content);
        courseService.saveAnnouncement(announcement);

        redirectAttributes.addFlashAttribute("successMessage", "E'lon e'lon qilindi!");
        return "redirect:/teacher/courses/" + id;
    }

    @PostMapping("/announcements/{id}/delete")
    public String deleteAnnouncement(@PathVariable Long id, @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        courseService.deleteAnnouncement(id);
        redirectAttributes.addFlashAttribute("successMessage", "E'lon o'chirildi!");
        return "redirect:/teacher/courses/" + courseId;
    }
}

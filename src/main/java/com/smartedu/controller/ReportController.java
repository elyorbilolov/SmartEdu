package com.smartedu.controller;

import com.smartedu.model.Course;
import com.smartedu.model.Role;
import com.smartedu.model.User;
import com.smartedu.service.CourseService;
import com.smartedu.service.QuizService;
import com.smartedu.service.ReportService;
import com.smartedu.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;
    private final CourseService courseService;
    private final QuizService quizService;

    public ReportController(ReportService reportService, UserService userService, CourseService courseService, QuizService quizService) {
        this.reportService = reportService;
        this.userService = userService;
        this.courseService = courseService;
        this.quizService = quizService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName()).orElse(null);
    }

    @GetMapping("/student/transcript")
    public ResponseEntity<byte[]> downloadMyTranscript() {
        User student = getCurrentUser();
        return downloadStudentTranscriptPdf(student);
    }

    @GetMapping("/student/{id}/transcript")
    public ResponseEntity<byte[]> downloadStudentTranscript(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        User student = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Talaba topilmadi: " + id));

        // Allow student themselves, teacher or admin
        if (currentUser.getRole() == Role.ROLE_STUDENT && !currentUser.getId().equals(student.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Ruxsat berilmagan");
        }

        return downloadStudentTranscriptPdf(student);
    }

    private ResponseEntity<byte[]> downloadStudentTranscriptPdf(User student) {
        try {
            byte[] pdf = reportService.generateStudentTranscriptPdf(student);
            String filename = "SmartEdu_Transcript_" + student.getUsername() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            throw new RuntimeException("PDF hisobot shakllantirishda xatolik: " + e.getMessage(), e);
        }
    }

    @GetMapping("/course/{id}/excel")
    public ResponseEntity<byte[]> downloadCourseGradesExcel(@PathVariable Long id) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));

        try {
            byte[] excelBytes = reportService.generateCourseGradesExcel(course);
            String rawFileName = "SmartEdu_Jurnal_" + course.getCode() + ".xlsx";
            String encodedFileName = URLEncoder.encode(rawFileName, StandardCharsets.UTF_8).replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            throw new RuntimeException("Excel hisobot shakllantirishda xatolik: " + e.getMessage(), e);
        }
    }

    @GetMapping("/analytics")
    public String analyticsPage(Model model) {
        model.addAttribute("totalStudents", userService.countStudents());
        model.addAttribute("totalTeachers", userService.countTeachers());
        model.addAttribute("totalCourses", courseService.countAllCourses());
        model.addAttribute("totalAssignments", courseService.countAllAssignments());
        model.addAttribute("totalSubmissions", courseService.countAllSubmissions());
        model.addAttribute("totalQuizzes", quizService.countAllQuizzes());

        List<Course> courses = courseService.findAllCourses();
        model.addAttribute("courses", courses);

        return "report/analytics";
    }
}

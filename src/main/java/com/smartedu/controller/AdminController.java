package com.smartedu.controller;

import com.smartedu.model.Course;
import com.smartedu.model.Role;
import com.smartedu.model.User;
import com.smartedu.service.CourseService;
import com.smartedu.service.QuizService;
import com.smartedu.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final QuizService quizService;

    public AdminController(UserService userService, CourseService courseService, QuizService quizService) {
        this.userService = userService;
        this.courseService = courseService;
        this.quizService = quizService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.countAll());
        model.addAttribute("totalStudents", userService.countStudents());
        model.addAttribute("totalTeachers", userService.countTeachers());
        model.addAttribute("totalCourses", courseService.countAllCourses());
        model.addAttribute("totalAssignments", courseService.countAllAssignments());
        model.addAttribute("totalSubmissions", courseService.countAllSubmissions());
        model.addAttribute("totalQuizzes", quizService.countAllQuizzes());

        List<User> recentUsers = userService.findAll();
        if (recentUsers.size() > 5) {
            recentUsers = recentUsers.subList(recentUsers.size() - 5, recentUsers.size());
        }
        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("recentAnnouncements", courseService.findRecentAnnouncements());

        return "admin/dashboard";
    }

    // User Management
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/new")
    public String createUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("fullName") String fullName,
                             @RequestParam("email") String email,
                             @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                             @RequestParam("role") Role role,
                             RedirectAttributes redirectAttributes) {
        if (userService.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ushbu login band: " + username);
            return "redirect:/admin/users/new";
        }

        try {
            userService.registerUser(username, password, fullName, email, phoneNumber, role);
            redirectAttributes.addFlashAttribute("successMessage", "Foydalanuvchi muvaffaqiyatli yaratildi!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xatolik: " + e.getMessage());
            return "redirect:/admin/users/new";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Foydalanuvchi topilmadi: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "admin/user-edit";
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @RequestParam("fullName") String fullName,
                             @RequestParam("email") String email,
                             @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                             @RequestParam("role") Role role,
                             @RequestParam(value = "enabled", defaultValue = "false") boolean enabled,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, fullName, email, phoneNumber, role, enabled);
            if (newPassword != null && !newPassword.isBlank()) {
                userService.updatePassword(id, newPassword);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Foydalanuvchi ma'lumotlari yangilandi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xatolik: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Foydalanuvchi o'chirildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Foydalanuvchini o'chirib bo'lmadi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Course Management
    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.findAllCourses());
        return "admin/courses";
    }

    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("teachers", userService.findTeachers());
        return "admin/course-form";
    }

    @PostMapping("/courses/new")
    public String createCourse(@RequestParam("title") String title,
                              @RequestParam("code") String code,
                              @RequestParam("category") String category,
                              @RequestParam("creditHours") int creditHours,
                              @RequestParam("semester") int semester,
                              @RequestParam(value = "teacherId", required = false) Long teacherId,
                              @RequestParam(value = "description", required = false) String description,
                              RedirectAttributes redirectAttributes) {
        try {
            User teacher = null;
            if (teacherId != null) {
                teacher = userService.findById(teacherId).orElse(null);
            }
            Course course = new Course(title, code, description, category, creditHours, semester, teacher);
            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Yangi fan muvaffaqiyatli qo'shildi!");
            return "redirect:/admin/courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xatolik: " + e.getMessage());
            return "redirect:/admin/courses/new";
        }
    }

    @GetMapping("/courses/{id}/edit")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.findCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));
        model.addAttribute("course", course);
        model.addAttribute("teachers", userService.findTeachers());
        return "admin/course-edit";
    }

    @PostMapping("/courses/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                               @RequestParam("title") String title,
                               @RequestParam("code") String code,
                               @RequestParam("category") String category,
                               @RequestParam("creditHours") int creditHours,
                               @RequestParam("semester") int semester,
                               @RequestParam(value = "teacherId", required = false) Long teacherId,
                               @RequestParam(value = "description", required = false) String description,
                               RedirectAttributes redirectAttributes) {
        try {
            Course course = courseService.findCourseById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + id));

            course.setTitle(title);
            course.setCode(code);
            course.setCategory(category);
            course.setCreditHours(creditHours);
            course.setSemester(semester);
            course.setDescription(description);

            if (teacherId != null) {
                User teacher = userService.findById(teacherId).orElse(null);
                course.setTeacher(teacher);
            } else {
                course.setTeacher(null);
            }

            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Fan ma'lumotlari yangilandi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xatolik: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Fan tizimdan o'chirildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fanni o'chirib bo'lmadi: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }
}

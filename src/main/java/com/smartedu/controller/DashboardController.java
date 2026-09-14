package com.smartedu.controller;

import com.smartedu.model.Course;
import com.smartedu.model.Role;
import com.smartedu.model.User;
import com.smartedu.service.CourseService;
import com.smartedu.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final CourseService courseService;

    public DashboardController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Course> featuredCourses = courseService.findAllCourses();
        if (featuredCourses.size() > 6) {
            featuredCourses = featuredCourses.subList(0, 6);
        }
        model.addAttribute("courses", featuredCourses);
        model.addAttribute("totalCourses", courseService.countAllCourses());
        model.addAttribute("totalStudents", userService.countStudents());
        model.addAttribute("totalTeachers", userService.countTeachers());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(auth.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() == Role.ROLE_ADMIN) {
            return "redirect:/admin/dashboard";
        } else if (user.getRole() == Role.ROLE_TEACHER) {
            return "redirect:/teacher/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }
}

package com.smartedu.controller;

import com.smartedu.model.Role;
import com.smartedu.model.User;
import com.smartedu.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Login yoki parol noto'g'ri kiritildi!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Tizimdan muvaffaqiyatli chiqdingiz.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 @RequestParam("fullName") String fullName,
                                 @RequestParam("email") String email,
                                 @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                 @RequestParam(value = "role", defaultValue = "ROLE_STUDENT") String roleStr,
                                 RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Parollar bir-biriga mos kelmadi!");
            return "redirect:/register";
        }

        if (userService.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ushbu login (" + username + ") allaqachon band!");
            return "redirect:/register";
        }

        Role role = Role.ROLE_STUDENT;
        if ("ROLE_TEACHER".equalsIgnoreCase(roleStr)) {
            role = Role.ROLE_TEACHER;
        }

        try {
            userService.registerUser(username, password, fullName, email, phoneNumber, role);
            redirectAttributes.addFlashAttribute("successMessage", "Ro'yxatdan muvaffaqiyatli o'tdingiz! Endi tizimga kiring.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xatolik: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("user", currentUser);
        return "auth/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("email") String email,
                                @RequestParam("phoneNumber") String phoneNumber,
                                @RequestParam(value = "newPassword", required = false) String newPassword,
                                RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName()).orElse(null);

        if (currentUser != null) {
            userService.updateUser(currentUser.getId(), fullName, email, phoneNumber, null, null);
            if (newPassword != null && !newPassword.isBlank()) {
                userService.updatePassword(currentUser.getId(), newPassword);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Profil ma'lumotlari muvaffaqiyatli yangilandi!");
        }
        return "redirect:/profile";
    }
}

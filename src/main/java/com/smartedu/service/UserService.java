package com.smartedu.service;

import com.smartedu.model.Role;
import com.smartedu.model.User;
import com.smartedu.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public List<User> findTeachers() {
        return userRepository.findByRoleOrderByFullNameAsc(Role.ROLE_TEACHER);
    }

    public List<User> findStudents() {
        return userRepository.findByRoleOrderByFullNameAsc(Role.ROLE_STUDENT);
    }

    @Transactional
    public User registerUser(String username, String rawPassword, String fullName, String email, String phoneNumber, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ushbu login band: " + username);
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, encodedPassword, fullName, email, phoneNumber, role);
        return userRepository.save(user);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, String fullName, String email, String phoneNumber, Role role, Boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Foydalanuvchi topilmadi ID: " + id));

        if (fullName != null && !fullName.isBlank()) user.setFullName(fullName);
        if (email != null) user.setEmail(email);
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);
        if (role != null) user.setRole(role);
        if (enabled != null) user.setEnabled(enabled);

        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Foydalanuvchi topilmadi ID: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long countAll() {
        return userRepository.count();
    }

    public long countStudents() {
        return userRepository.countByRole(Role.ROLE_STUDENT);
    }

    public long countTeachers() {
        return userRepository.countByRole(Role.ROLE_TEACHER);
    }
}

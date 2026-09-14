package com.smartedu.service;

import com.smartedu.model.*;
import com.smartedu.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final MaterialRepository materialRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final AnnouncementRepository announcementRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final FileStorageService fileStorageService;

    public CourseService(CourseRepository courseRepository,
                         LessonRepository lessonRepository,
                         MaterialRepository materialRepository,
                         AssignmentRepository assignmentRepository,
                         SubmissionRepository submissionRepository,
                         AnnouncementRepository announcementRepository,
                         LessonProgressRepository lessonProgressRepository,
                         FileStorageService fileStorageService) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.materialRepository = materialRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.announcementRepository = announcementRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.fileStorageService = fileStorageService;
    }

    // Course operations
    public List<Course> findAllCourses() {
        return courseRepository.findAllByOrderByTitleAsc();
    }

    public List<Course> searchCourses(String query) {
        if (query == null || query.isBlank()) {
            return findAllCourses();
        }
        return courseRepository.searchCourses(query.trim());
    }

    public Optional<Course> findCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> findCoursesByTeacher(User teacher) {
        return courseRepository.findByTeacherOrderByTitleAsc(teacher);
    }

    public List<Course> findCoursesByStudent(User student) {
        return courseRepository.findCoursesByStudent(student);
    }

    @Transactional
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Transactional
    public boolean enrollStudent(Long courseId, User student) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + courseId));

        if (!course.getStudents().contains(student)) {
            course.getStudents().add(student);
            student.getEnrolledCourses().add(course);
            courseRepository.save(course);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean unenrollStudent(Long courseId, User student) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi: " + courseId));

        if (course.getStudents().contains(student)) {
            course.getStudents().remove(student);
            student.getEnrolledCourses().remove(course);
            courseRepository.save(course);
            return true;
        }
        return false;
    }

    public boolean isStudentEnrolled(Course course, User student) {
        if (course == null || student == null) return false;
        return course.getStudents().stream().anyMatch(s -> s.getId().equals(student.getId()));
    }

    // Lesson operations
    public List<Lesson> findLessonsByCourse(Course course) {
        return lessonRepository.findByCourseOrderByOrderIndexAsc(course);
    }

    public Optional<Lesson> findLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    @Transactional
    public Lesson saveLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    @Transactional
    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }

    // Material operations
    public Optional<Material> findMaterialById(Long id) {
        return materialRepository.findById(id);
    }

    @Transactional
    public Material addMaterialWithFile(Lesson lesson, String title, MultipartFile file) {
        String storedFilename = fileStorageService.storeFile(file);
        String originalFilename = file.getOriginalFilename();
        String fileType = "FILE";
        if (originalFilename != null) {
            String lower = originalFilename.toLowerCase();
            if (lower.endsWith(".pdf")) fileType = "PDF";
            else if (lower.endsWith(".ppt") || lower.endsWith(".pptx")) fileType = "PPTX";
            else if (lower.endsWith(".doc") || lower.endsWith(".docx")) fileType = "DOCX";
            else if (lower.endsWith(".zip") || lower.endsWith(".rar")) fileType = "ZIP";
            else if (lower.endsWith(".mp4") || lower.endsWith(".mkv")) fileType = "VIDEO";
        }

        Material material = new Material(lesson, title, originalFilename, storedFilename, fileType, file.getSize());
        return materialRepository.save(material);
    }

    @Transactional
    public Material addMaterialWithLink(Lesson lesson, String title, String externalUrl) {
        Material material = new Material(lesson, title, externalUrl, "LINK");
        return materialRepository.save(material);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        materialRepository.findById(id).ifPresent(m -> {
            if (m.getFilePath() != null) {
                fileStorageService.deleteFile(m.getFilePath());
            }
            materialRepository.delete(m);
        });
    }

    // Assignment operations
    public List<Assignment> findAssignmentsByCourse(Course course) {
        return assignmentRepository.findByCourseOrderByCreatedAtDesc(course);
    }

    public Optional<Assignment> findAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    @Transactional
    public Assignment saveAssignment(Assignment assignment, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String stored = fileStorageService.storeFile(file);
            assignment.setAttachmentFileName(file.getOriginalFilename());
            assignment.setAttachmentPath(stored);
        }
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    // Submission operations
    public Optional<Submission> findSubmissionByAssignmentAndStudent(Assignment assignment, User student) {
        return submissionRepository.findByAssignmentAndStudent(assignment, student);
    }

    public List<Submission> findSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignmentOrderBySubmittedAtDesc(assignment);
    }

    public List<Submission> findSubmissionsByStudent(User student) {
        return submissionRepository.findByStudentOrderBySubmittedAtDesc(student);
    }

    public List<Submission> findSubmissionsByCourseAndStudent(Course course, User student) {
        return submissionRepository.findByCourseAndStudent(course, student);
    }

    public List<Submission> findSubmissionsByCourse(Course course) {
        return submissionRepository.findByCourse(course);
    }

    public Optional<Submission> findSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    @Transactional
    public Submission submitAssignment(Assignment assignment, User student, String text, MultipartFile file) {
        Submission submission = submissionRepository.findByAssignmentAndStudent(assignment, student)
                .orElse(new Submission());

        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedText(text);
        submission.setSubmittedAt(LocalDateTime.now());

        if (assignment.getDeadline() != null && LocalDateTime.now().isAfter(assignment.getDeadline())) {
            submission.setStatus(SubmissionStatus.LATE);
        } else {
            submission.setStatus(SubmissionStatus.SUBMITTED);
        }

        if (file != null && !file.isEmpty()) {
            String stored = fileStorageService.storeFile(file);
            submission.setFileName(file.getOriginalFilename());
            submission.setFilePath(stored);
        }

        return submissionRepository.save(submission);
    }

    @Transactional
    public Submission gradeSubmission(Long submissionId, int score, String feedback, User teacher) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Yechim topilmadi: " + submissionId));

        submission.setScore(score);
        submission.setTeacherFeedback(feedback);
        submission.setGradedBy(teacher);
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.GRADED);

        return submissionRepository.save(submission);
    }

    // Announcements
    public List<Announcement> findAnnouncementsByCourse(Course course) {
        return announcementRepository.findByCourseOrderByCreatedAtDesc(course);
    }

    public List<Announcement> findRecentAnnouncements() {
        return announcementRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Transactional
    public Announcement saveAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    // Progress
    @Transactional
    public void toggleLessonProgress(User student, Lesson lesson) {
        LessonProgress progress = lessonProgressRepository.findByStudentAndLesson(student, lesson)
                .orElse(new LessonProgress(student, lesson, false));

        boolean newState = !progress.isCompleted();
        progress.setCompleted(newState);
        progress.setCompletedAt(newState ? LocalDateTime.now() : null);
        lessonProgressRepository.save(progress);
    }

    public boolean isLessonCompleted(User student, Lesson lesson) {
        return lessonProgressRepository.findByStudentAndLesson(student, lesson)
                .map(LessonProgress::isCompleted)
                .orElse(false);
    }

    public int getCourseProgressPercentage(User student, Course course) {
        long totalLessons = lessonRepository.countByCourse(course);
        if (totalLessons == 0) return 0;
        long completed = lessonProgressRepository.countCompletedByStudentAndCourse(student, course);
        return (int) Math.round(((double) completed / totalLessons) * 100.0);
    }

    public long countAllCourses() {
        return courseRepository.count();
    }

    public long countAllAssignments() {
        return assignmentRepository.count();
    }

    public long countAllSubmissions() {
        return submissionRepository.count();
    }
}

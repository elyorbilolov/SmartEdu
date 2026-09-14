package com.smartedu.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @NotBlank(message = "Material nomi kiritilishi shart")
    @Column(nullable = false)
    private String title;

    private String fileName;

    private String filePath;

    private String fileType; // PDF, PPTX, DOCX, ZIP, LINK

    private Long fileSize;

    private String externalUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Material() {
    }

    public Material(Lesson lesson, String title, String fileName, String filePath, String fileType, Long fileSize) {
        this.lesson = lesson;
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.createdAt = LocalDateTime.now();
    }

    public Material(Lesson lesson, String title, String externalUrl, String fileType) {
        this.lesson = lesson;
        this.title = title;
        this.externalUrl = externalUrl;
        this.fileType = fileType;
        this.createdAt = LocalDateTime.now();
    }

    // Helper for readable file size
    public String getFormattedSize() {
        if (fileSize == null || fileSize == 0) return "-";
        if (fileSize < 1024) return fileSize + " B";
        int exp = (int) (Math.log(fileSize) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", fileSize / Math.pow(1024, exp), pre);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

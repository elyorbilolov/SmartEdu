package com.smartedu.controller;

import com.smartedu.model.Assignment;
import com.smartedu.model.Material;
import com.smartedu.model.Submission;
import com.smartedu.service.CourseService;
import com.smartedu.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class MaterialController {

    private final CourseService courseService;
    private final FileStorageService fileStorageService;

    public MaterialController(CourseService courseService, FileStorageService fileStorageService) {
        this.courseService = courseService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/materials/download/{id}")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long id) {
        Material material = courseService.findMaterialById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material topilmadi: " + id));

        Resource resource = fileStorageService.loadFileAsResource(material.getFilePath());
        String filename = URLEncoder.encode(material.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + filename)
                .body(resource);
    }

    @GetMapping("/assignments/download/{id}")
    public ResponseEntity<Resource> downloadAssignmentFile(@PathVariable Long id) {
        Assignment assignment = courseService.findAssignmentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topshiriq topilmadi: " + id));

        Resource resource = fileStorageService.loadFileAsResource(assignment.getAttachmentPath());
        String filename = URLEncoder.encode(assignment.getAttachmentFileName(), StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + filename)
                .body(resource);
    }

    @GetMapping("/submissions/download/{id}")
    public ResponseEntity<Resource> downloadSubmissionFile(@PathVariable Long id) {
        Submission submission = courseService.findSubmissionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Yechim topilmadi: " + id));

        Resource resource = fileStorageService.loadFileAsResource(submission.getFilePath());
        String filename = URLEncoder.encode(submission.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + filename)
                .body(resource);
    }
}

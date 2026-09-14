package com.smartedu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Fayllar saqlash jildini yaratib bo'lmadi: " + uploadDir, e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String storedFilename = UUID.randomUUID().toString() + extension;

        try {
            if (storedFilename.contains("..")) {
                throw new IllegalArgumentException("Noto'g'ri fayl yo'li: " + storedFilename);
            }

            Path targetLocation = rootLocation.resolve(storedFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Faylni saqlashda xatolik yuz berdi: " + originalFilename, e);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = rootLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Fayl topilmadi yoki o'qib bo'lmadi: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Fayl topilmadi: " + filename, e);
        }
    }

    public boolean deleteFile(String filename) {
        if (filename == null || filename.isBlank()) return false;
        try {
            Path filePath = rootLocation.resolve(filename).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
}

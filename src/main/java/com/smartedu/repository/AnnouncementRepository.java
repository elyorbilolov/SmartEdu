package com.smartedu.repository;

import com.smartedu.model.Announcement;
import com.smartedu.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourseOrderByCreatedAtDesc(Course course);
    List<Announcement> findTop5ByOrderByCreatedAtDesc();
}

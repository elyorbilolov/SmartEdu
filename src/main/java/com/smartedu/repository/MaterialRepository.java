package com.smartedu.repository;

import com.smartedu.model.Lesson;
import com.smartedu.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByLessonOrderByIdAsc(Lesson lesson);
}

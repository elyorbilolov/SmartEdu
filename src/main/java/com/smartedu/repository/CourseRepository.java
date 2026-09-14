package com.smartedu.repository;

import com.smartedu.model.Course;
import com.smartedu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findByTeacherOrderByTitleAsc(User teacher);
    List<Course> findAllByOrderByTitleAsc();

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s = :student ORDER BY c.title ASC")
    List<Course> findCoursesByStudent(@Param("student") User student);

    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Course> searchCourses(@Param("query") String query);
}

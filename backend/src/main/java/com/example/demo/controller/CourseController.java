package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.Section;
import com.example.demo.service.CourseService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    public Course createCourse(@RequestBody Map<String, String> body) {
        return courseService.createCourse(body.get("title"), body.get("description"));
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return courseService.updateCourse(id, body.get("title"), body.get("description"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public Section addSection(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return courseService.addSection(id, body.get("content"));
    }

    @DeleteMapping("/{courseId}/sections/{sectionId}")
    public ResponseEntity<Void> removeSection(@PathVariable Long courseId, @PathVariable Long sectionId) {
        courseService.removeSection(courseId, sectionId);
        return ResponseEntity.noContent().build();
    }
}

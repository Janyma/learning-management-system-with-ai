package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Section;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.SectionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;

    public CourseService(CourseRepository courseRepository, SectionRepository sectionRepository) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public Course createCourse(String title, String description) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, String title, String description) {
        Course course = getCourseById(id);
        course.setTitle(title);
        course.setDescription(description);
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public Section addSection(Long courseId, String content) {
        Course course = getCourseById(courseId);
        Section section = new Section();
        section.setContent(content);
        section.setCourse(course);
        return sectionRepository.save(section);
    }

    public void removeSection(Long courseId, Long sectionId) {
        Course course = getCourseById(courseId);
        course.getSections().removeIf(s -> s.getId().equals(sectionId));
        courseRepository.save(course);
    }

    public List<Section> getSectionsByCourseId(Long courseId) {
        Course course = getCourseById(courseId);
        return course.getSections();
    }
}

package edu.ccrm.service;

import edu.ccrm.domain.Course;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CourseService {

    private final Map<String, Course> coursesByCode = new HashMap<>();

    public boolean addCourse(Course course) {
        if (coursesByCode.containsKey(course.getCode())) {
            System.out.println("Error: Course with code " + course.getCode() + " already exists.");
            return false;
        }
        coursesByCode.put(course.getCode(), course);
        System.out.println("Course added successfully: " + course.getTitle());
        return true;
    }

    public Optional<Course> findCourseByCode(String courseCode) {
        return Optional.ofNullable(coursesByCode.get(courseCode));
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(coursesByCode.values());
    }
    public void loadCourses(List<Course> courses) {
        this.coursesByCode.clear();
        for (Course course : courses) {
            this.coursesByCode.put(course.getCode(), course);
        }
    }
}
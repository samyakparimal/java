package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Instructor extends Person {

    private String department;
    private List<Course> assignedCourses;

    public Instructor(int id, String fullName, String email, LocalDate dateOfBirth, String department) {
        super(id, fullName, email, dateOfBirth);
        this.department = department;
        this.assignedCourses = new ArrayList<>();
    }

    @Override
    public String getProfile() {
        return String.format("--- Instructor Profile ---\nID: %d\nName: %s\nDepartment: %s\nAssigned Courses: %d",
                getId(), getFullName(), getDepartment(), assignedCourses.size());
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Course> getAssignedCourses() {
        return assignedCourses;
    }



    public void assignCourse(Course course) {
        this.assignedCourses.add(course);
    }
}
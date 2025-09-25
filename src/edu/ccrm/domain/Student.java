package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person {

    private String regNo;
    private StudentStatus status;
    private List<Enrollment> enrolledCourses;

    public Student(int id, String regNo, String fullName, String email, LocalDate dateOfBirth) {
        super(id, fullName, email, dateOfBirth);
        this.regNo = regNo;
        this.status = StudentStatus.ACTIVE;
        this.enrolledCourses = new ArrayList<>();
    }

    @Override
    public String getProfile() {
        return String.format("--- Student Profile ---\nID: %d\nReg No: %s\nName: %s\nStatus: %s\nEnrolled Courses: %d",
                getId(), getRegNo(), getFullName(), getStatus(), enrolledCourses.size());
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }



    public List<Enrollment> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrolledCourses.add(enrollment);
    }

    public enum StudentStatus {
        ACTIVE,
        INACTIVE,
        GRADUATED
    }
    @Override
    public String toString() {
        return "Student -> ID: " + getId() + ", Reg No: " + regNo + ", Name: " + getFullName();
    }
}
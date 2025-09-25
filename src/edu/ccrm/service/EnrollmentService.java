package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Student;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentService {

    public boolean enroll(Student student, Course course) {
        boolean alreadyEnrolled = student.getEnrolledCourses().stream()
                .anyMatch(enrollment -> enrollment.getCourse().equals(course));

        if (alreadyEnrolled) {
            System.out.println("Error: Student " + student.getFullName() + " is already enrolled in " + course.getTitle() + ".");
            return false;
        }

        Enrollment newEnrollment = new Enrollment(student, course);
        student.addEnrollment(newEnrollment);
        System.out.println("Enrollment successful!");
        return true;
    }

    public void assignGrade(Student student, Course course, Grade grade) {
        student.getEnrolledCourses().stream()
                .filter(enrollment -> enrollment.getCourse().equals(course))
                .findFirst()
                .ifPresent(enrollment -> {
                    enrollment.setGrade(grade);
                    System.out.println("Grade " + grade + " assigned to " + student.getFullName() + " for " + course.getTitle() + ".");
                });
    }

    public double calculateGpa(Student student) {
        List<Enrollment> gradedCourses = student.getEnrolledCourses().stream()
                .filter(e -> e.getGrade() != Grade.NOT_GRADED)
                .collect(Collectors.toList());

        if (gradedCourses.isEmpty()) {
            return 0.0;
        }

        double totalPoints = gradedCourses.stream()
                .mapToDouble(e -> e.getGrade().getGradePoint() * e.getCourse().getCredits())
                .sum();

        double totalCredits = gradedCourses.stream()
                .mapToDouble(e -> e.getCourse().getCredits())
                .sum();

        return totalPoints / totalCredits;
    }
}
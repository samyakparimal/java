package edu.ccrm.service;

import edu.ccrm.domain.Student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentService {

    private final Map<String, Student> studentsByRegNo = new HashMap<>();

    public boolean addStudent(Student student) {
        if (studentsByRegNo.containsKey(student.getRegNo())) {
            System.out.println("Error: Student with registration number " + student.getRegNo() + " already exists.");
            return false;
        }
        studentsByRegNo.put(student.getRegNo(), student);
        System.out.println("Student added successfully: " + student.getFullName());
        return true;
    }

    public Optional<Student> findStudentByRegNo(String regNo) {
        return Optional.ofNullable(studentsByRegNo.get(regNo));
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(studentsByRegNo.values());
    }
    public void loadStudents(List<Student> students) {
        this.studentsByRegNo.clear();
        for (Student student : students) {
            this.studentsByRegNo.put(student.getRegNo(), student);
        }
    }
}
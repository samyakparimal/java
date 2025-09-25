package edu.ccrm.io;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Student;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.EnrollmentService;
import edu.ccrm.service.StudentService;
import edu.ccrm.util.RecursiveUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;

public class FileService {

    private final Path dataDirectory = Paths.get("data");

    public void exportStudents(List<Student> students) {
        try {
            Files.createDirectories(dataDirectory);
            Path filePath = dataDirectory.resolve("students.csv");

            List<String> lines = students.stream()
                    .map(s -> String.join(",",
                            String.valueOf(s.getId()),
                            s.getRegNo(),
                            s.getFullName(),
                            s.getEmail()))
                    .collect(Collectors.toList());

            lines.add(0, "id,regNo,fullName,email");

            Files.write(filePath, lines);
            System.out.println("Student data successfully exported to " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting student data: " + e.getMessage());
        }
    }

    public void exportCourses(List<Course> courses) {
        try {
            Files.createDirectories(dataDirectory);
            Path filePath = dataDirectory.resolve("courses.csv");

            List<String> lines = courses.stream()
                    .map(c -> String.join(",",
                            c.getCode(),
                            c.getTitle(),
                            String.valueOf(c.getCredits())))
                    .collect(Collectors.toList());

            lines.add(0, "code,title,credits");

            Files.write(filePath, lines);
            System.out.println("Course data successfully exported to " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting course data: " + e.getMessage());
        }
    }

    public void exportEnrollments(List<Student> students) {
        try {
            Files.createDirectories(dataDirectory);
            Path filePath = dataDirectory.resolve("enrollments.csv");

            List<String> lines = students.stream()
                    .flatMap(student -> student.getEnrolledCourses().stream())
                    .map(enrollment -> String.join(",",
                            enrollment.getStudent().getRegNo(),
                            enrollment.getCourse().getCode(),
                            enrollment.getGrade().name()))
                    .collect(Collectors.toList());

            lines.add(0, "studentRegNo,courseCode,grade");

            Files.write(filePath, lines);
            System.out.println("Enrollment data successfully exported to " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting enrollment data: " + e.getMessage());
        }
    }

    public List<Student> importStudents() {
        Path filePath = dataDirectory.resolve("students.csv");
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1)
                    .map(line -> {
                        String[] parts = line.split(",");
                        int id = Integer.parseInt(parts[0]);
                        return new Student(id, parts[1], parts[2], parts[3], java.time.LocalDate.of(2000, 1, 1));
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error importing student data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Course> importCourses() {
        Path filePath = dataDirectory.resolve("courses.csv");
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(1)
                    .map(line -> {
                        String[] parts = line.split(",");
                        return new Course.CourseBuilder(parts[0], parts[1])
                                .credits(Integer.parseInt(parts[2]))
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error importing course data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void importEnrollments(StudentService studentService, CourseService courseService, EnrollmentService enrollmentService) {
        Path filePath = dataDirectory.resolve("enrollments.csv");
        if (!Files.exists(filePath)) {
            return;
        }

        try (Stream<String> lines = Files.lines(filePath)) {
            lines.skip(1)
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        String studentRegNo = parts[0];
                        String courseCode = parts[1];
                        Grade grade = Grade.valueOf(parts[2]);

                        var studentOpt = studentService.findStudentByRegNo(studentRegNo);
                        var courseOpt = courseService.findCourseByCode(courseCode);

                        if (studentOpt.isPresent() && courseOpt.isPresent()) {
                            enrollmentService.enroll(studentOpt.get(), courseOpt.get());
                            enrollmentService.assignGrade(studentOpt.get(), courseOpt.get(), grade);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error importing enrollment data: " + e.getMessage());
        }
    }

    public Path backupData() throws IOException {
        String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                .format(java.time.LocalDateTime.now());

        Path backupDir = Paths.get("backup", "backup_" + timestamp);
        Files.createDirectories(backupDir);

        try (Stream<Path> stream = Files.walk(dataDirectory)) {
            stream
                    .filter(path -> !path.equals(dataDirectory))
                    .forEach(source -> {
                        try {
                            Path destination = backupDir.resolve(dataDirectory.relativize(source));
                            Files.copy(source, destination);
                        } catch (IOException e) {
                            System.err.println("Failed to copy " + source + ": " + e.getMessage());
                        }
                    });
        }
        System.out.println("Backup created successfully at: " + backupDir);
        return backupDir;
    }
}
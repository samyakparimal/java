package edu.ccrm.cli;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Student;
import edu.ccrm.io.FileService;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.EnrollmentService;
import edu.ccrm.service.StudentService;
import edu.ccrm.util.RecursiveUtils;
import java.nio.file.Path;

import java.util.List;
import java.util.Scanner;

public class CCRMApp {

    private static final StudentService studentService = new StudentService();
    private static final CourseService courseService = new CourseService();
    private static final EnrollmentService enrollmentService = new EnrollmentService();
    private static final FileService fileService = new FileService();
    private static int nextStudentId = 1;

    public static void main(String[] args) {
        System.out.println("Loading existing data...");
        List<Student> students = fileService.importStudents();
        studentService.loadStudents(students);
        List<Course> courses = fileService.importCourses();
        courseService.loadCourses(courses);

        fileService.importEnrollments(studentService, courseService, enrollmentService);

        if (!students.isEmpty()) {
            nextStudentId = students.stream().mapToInt(Student::getId).max().orElse(0) + 1;
        }
        System.out.println("Data loaded successfully.");


        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Campus Course & Records Manager (CCRM)");

        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    manageStudents(scanner);
                    break;
                case 2:
                    manageCourses(scanner);
                    break;
                case 3:
                    manageEnrollments(scanner);
                    break;
                case 5:
                    try {
                        Path backupPath = fileService.backupData();
                        long size = RecursiveUtils.calculateDirectorySize(backupPath);
                        System.out.printf("Total backup size: %.2f KB\n", size / 1024.0);
                    } catch (java.io.IOException e) {
                        System.err.println("Backup failed: " + e.getMessage());
                    }
                    break;
                case 7:
                    fileService.exportStudents(studentService.getAllStudents());
                    fileService.exportCourses(courseService.getAllCourses());
                    fileService.exportEnrollments(studentService.getAllStudents());

                    running = false;
                    System.out.println("Thank you for using CCRM. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n------------------------------------------------------");
        System.out.println("Main Menu");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Enrollments & Grades");
        System.out.println("5. Backup Data & Show Size");
        System.out.println("7. Exit");
        System.out.println("------------------------------------------------------");
    }

    private static void manageStudents(Scanner scanner) {
        boolean back = false;
        while (!back) {
            try {
                System.out.println("\n--- Student Management ---");
                System.out.println("1. Add New Student");
                System.out.println("2. List All Students");
                System.out.println("3. Find Student by Registration Number");
                System.out.println("4. Print Student Transcript");
                System.out.println("5. Back to Main Menu");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: addStudent(scanner); break;
                    case 2: listAllStudents(); break;
                    case 3: findStudent(scanner); break;
                    case 4: printStudentTranscript(scanner); break;
                    case 5: back = true; break;
                    default: System.out.println("Invalid choice. Please try again."); break;
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }


    private static void addStudent(Scanner scanner) {
        System.out.println("\n--- Add New Student ---");
        System.out.print("Enter Registration Number (e.g., S001): ");
        String regNo = scanner.nextLine();

        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        Student newStudent = new Student(nextStudentId++, regNo, fullName, email, java.time.LocalDate.of(2000, 1, 1));

        studentService.addStudent(newStudent);
    }

    private static void listAllStudents() {
        System.out.println("\n--- List of All Students ---");
        List<Student> students = studentService.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("No students found in the system.");
        } else {
            for (Student student : students) {
                System.out.println(student);
            }
        }
    }

    private static void findStudent(Scanner scanner) {
        System.out.println("\n--- Find a Student ---");
        System.out.print("Enter Registration Number to find: ");
        String regNo = scanner.nextLine();

        studentService.findStudentByRegNo(regNo)
                .ifPresentOrElse(
                        student -> System.out.println("Student Found:\n" + student.getProfile()),
                        () -> System.out.println("No student found with Registration Number: " + regNo)
                );
    }

    private static void manageCourses(Scanner scanner) {
        boolean back = false;
        while (!back) {
            try {
                System.out.println("\n--- Course Management ---");
                System.out.println("1. Add New Course");
                System.out.println("2. List All Courses");
                System.out.println("3. Find Course by Code");
                System.out.println("4. Back to Main Menu");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        addCourse(scanner);
                        break;
                    case 2:
                        listAllCourses();
                        break;
                    case 3:
                        findCourse(scanner);
                        break;
                    case 4:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
    private static void addCourse(Scanner scanner) {
        System.out.println("\n--- Add New Course ---");
        System.out.print("Enter Course Code (e.g., CS101): ");
        String code = scanner.nextLine();

        System.out.print("Enter Course Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Credits: ");
        int credits = scanner.nextInt();
        scanner.nextLine();

        Course newCourse = new Course.CourseBuilder(code, title)
                .credits(credits)
                .build();

        courseService.addCourse(newCourse);
    }

    private static void listAllCourses() {
        System.out.println("\n--- List of All Courses ---");
        List<Course> courses = courseService.getAllCourses();

        if (courses.isEmpty()) {
            System.out.println("No courses found in the system.");
        } else {
            for (Course course : courses) {
                System.out.println(course);
            }
        }
    }

    private static void findCourse(Scanner scanner) {
        System.out.println("\n--- Find a Course ---");
        System.out.print("Enter Course Code to find: ");
        String code = scanner.nextLine();

        courseService.findCourseByCode(code)
                .ifPresentOrElse(
                        course -> System.out.println("Course Found: " + course),
                        () -> System.out.println("No course found with Code: " + code)
                );
    }
    private static void manageEnrollments(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Enrollments & Grades ---");
            System.out.println("1. Enroll Student in Course");
            System.out.println("2. Assign Grade to Student");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    enrollStudent(scanner);
                    break;
                case 2:
                    assignGrade(scanner);
                    break;
                case 3:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private static void enrollStudent(Scanner scanner) {
        System.out.println("\n--- Enroll Student in Course ---");
        System.out.print("Enter Student Registration Number: ");
        String regNo = scanner.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        var studentOpt = studentService.findStudentByRegNo(regNo);
        var courseOpt = courseService.findCourseByCode(courseCode);

        if (studentOpt.isPresent() && courseOpt.isPresent()) {
            Student student = studentOpt.get();
            Course course = courseOpt.get();
            enrollmentService.enroll(student, course);
        } else {
            if (studentOpt.isEmpty()) {
                System.out.println("Error: Student with registration number " + regNo + " not found.");
            }
            if (courseOpt.isEmpty()) {
                System.out.println("Error: Course with code " + courseCode + " not found.");
            }
        }
    }

    private static void assignGrade(Scanner scanner) {
        System.out.println("\n--- Assign Grade ---");
        System.out.print("Enter Student Registration Number: ");
        String regNo = scanner.nextLine();
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        var studentOpt = studentService.findStudentByRegNo(regNo);
        var courseOpt = courseService.findCourseByCode(courseCode);

        if (studentOpt.isPresent() && courseOpt.isPresent()) {
            System.out.print("Enter Grade (S, A, B, C, D, E, F): ");
            String gradeStr = scanner.nextLine().toUpperCase();
            try {
                Grade grade = Grade.valueOf(gradeStr);
                enrollmentService.assignGrade(studentOpt.get(), courseOpt.get(), grade);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Invalid grade entered. Please use S, A, B, etc.");
            }
        } else {
            System.out.println("Error: Student or Course not found.");
        }
    }
    private static void printStudentTranscript(Scanner scanner) {
        System.out.println("\n--- Print Student Transcript ---");
        System.out.print("Enter Student Registration Number: ");
        String regNo = scanner.nextLine();

        studentService.findStudentByRegNo(regNo).ifPresentOrElse(student -> {
            System.out.println("\n-------------------- TRANSCRIPT --------------------");
            System.out.println(student.getProfile());
            System.out.println("\nCourses Enrolled:");
            if (student.getEnrolledCourses().isEmpty()) {
                System.out.println("No courses enrolled.");
            } else {
                student.getEnrolledCourses().forEach(enrollment -> {
                    System.out.printf("  - %s: %s (%d credits) - Grade: %s\n",
                            enrollment.getCourse().getCode(),
                            enrollment.getCourse().getTitle(),
                            enrollment.getCourse().getCredits(),
                            enrollment.getGrade().name());
                });
            }

            double gpa = enrollmentService.calculateGpa(student);
            System.out.printf("\nCumulative GPA: %.2f\n", gpa);
            System.out.println("--------------------------------------------------");

        }, () -> System.out.println("Student not found with Registration Number: " + regNo));
    }
}
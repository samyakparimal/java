package edu.ccrm.domain;

public final class Course {

    private final String code;
    private final String title;
    private final int credits;
    private final String department;
    private final Semester semester;
    private Instructor instructor;

    private Course(CourseBuilder builder) {
        this.code = builder.code;
        this.title = builder.title;
        this.credits = builder.credits;
        this.department = builder.department;
        this.semester = builder.semester;
        this.instructor = builder.instructor;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getDepartment() { return department; }
    public Semester getSemester() { return semester; }
    public Instructor getInstructor() { return instructor; }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public static class CourseBuilder {
        private final String code;
        private final String title;

        private int credits = 3;
        private String department = "General";
        private Semester semester = Semester.FALL;
        private Instructor instructor = null;

        public CourseBuilder(String code, String title) {
            this.code = code;
            this.title = title;
        }

        public CourseBuilder credits(int credits) {
            this.credits = credits;
            return this;
        }

        public CourseBuilder department(String department) {
            this.department = department;
            return this;
        }

        public CourseBuilder semester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public CourseBuilder instructor(Instructor instructor) {
            this.instructor = instructor;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
    @Override
    public String toString() {
        String instructorName = (instructor != null) ? instructor.getFullName() : "Not Assigned";

        return String.format("Course[Code=%s, Title='%s', Credits=%d, Instructor=%s]",
                code, title, credits, instructorName);
    }
}
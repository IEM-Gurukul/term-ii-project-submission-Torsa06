package model;

public class Student extends Person {

    private String email;
    private String course;

    public Student(String studentId, String name, String email, String course) {
        super(studentId, name);
        this.email  = email;
        this.course = course;
    }

    public String getEmail()  { return email; }
    public String getCourse() { return course; }

    public void setEmail(String email)   { this.email  = email; }
    public void setCourse(String course) { this.course = course; }

    @Override
    public String getDetails() {
        return String.format("Student[ID=%s | Name=%-20s | Course=%-15s | Email=%s]",
                getId(), getName(), course, email);
    }

    public String toCsvLine() {
        return getId() + "," + getName() + "," + email + "," + course;
    }

    public static Student fromCsvLine(String line) {
        String[] parts = line.split(",", 4);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Malformed student CSV line: " + line);
        }
        return new Student(parts[0].trim(), parts[1].trim(),
                           parts[2].trim(), parts[3].trim());
    }
}

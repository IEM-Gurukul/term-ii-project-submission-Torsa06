package ui;

import model.AttendanceRecord;
import model.AttendanceRecord.Status;
import model.Student;
import java.util.List;

public class ConsoleUI {

    private static final String LINE  = "─".repeat(65);
    private static final String DLINE = "═".repeat(65);

    public void showMainMenu() {
        System.out.println("\n" + DLINE);
        System.out.println("   Smart Student Attendance Management System");
        System.out.println(DLINE);
        System.out.println("  1. Student Management");
        System.out.println("  2. Attendance Management");
        System.out.println("  3. Reports & Analytics");
        System.out.println("  0. Exit");
        System.out.println(DLINE);
    }

    public void showStudentMenu() {
        System.out.println("\n" + LINE);
        System.out.println("  Student Management");
        System.out.println(LINE);
        System.out.println("  1. Register New Student");
        System.out.println("  2. View All Students");
        System.out.println("  3. Search Student by ID");
        System.out.println("  4. Search Students by Name");
        System.out.println("  5. Remove Student");
        System.out.println("  0. Back to Main Menu");
        System.out.println(LINE);
    }

    public void showAttendanceMenu() {
        System.out.println("\n" + LINE);
        System.out.println("  Attendance Management");
        System.out.println(LINE);
        System.out.println("  1. Mark Attendance (single student)");
        System.out.println("  2. Mark Attendance (all students – today)");
        System.out.println("  3. Update Attendance Record");
        System.out.println("  4. View Attendance History (by student)");
        System.out.println("  5. View Attendance by Date");
        System.out.println("  0. Back to Main Menu");
        System.out.println(LINE);
    }

    public void showReportsMenu() {
        System.out.println("\n" + LINE);
        System.out.println("  Reports & Analytics");
        System.out.println(LINE);
        System.out.println("  1. Attendance Percentage – Single Student");
        System.out.println("  2. Full Attendance Summary – All Students");
        System.out.println("  3. Low Attendance Alert");
        System.out.println("  0. Back to Main Menu");
        System.out.println(LINE);
    }
    public void printStudent(Student s) {
        System.out.println("  " + s.getDetails());
    }

    public void printStudentList(List<Student> students) {
        if (students.isEmpty()) {
            info("No students found.");
            return;
        }
        System.out.println("\n  " + LINE);
        System.out.printf("  %-10s %-20s %-15s %s%n",
                "ID", "Name", "Course", "Email");
        System.out.println("  " + LINE);
        for (Student s : students) {
            System.out.printf("  %-10s %-20s %-15s %s%n",
                    s.getId(), s.getName(), s.getCourse(), s.getEmail());
        }
        System.out.println("  " + LINE);
        System.out.println("  Total: " + students.size() + " student(s)");
    }

    public void printAttendanceList(List<AttendanceRecord> records) {
        if (records.isEmpty()) {
            info("No attendance records found.");
            return;
        }
        System.out.println();
        System.out.printf("  %-12s %-12s %s%n", "Date", "Student ID", "Status");
        System.out.println("  " + LINE);
        for (AttendanceRecord r : records) {
            String mark = r.isPresent() ? "✔ PRESENT" : "✘ ABSENT";
            System.out.printf("  %-12s %-12s %s%n",
                    r.getDate(), r.getStudentId(), mark);
        }
    }

    public void printAttendanceSummary(String studentId, String name,
                                       double percentage, int total, int present) {
        System.out.printf("  %-10s %-20s  %3d/%3d  %6.1f%%%n",
                studentId, name, present, total, percentage);
    }

    public void printSummaryHeader() {
        System.out.println("\n  " + LINE);
        System.out.printf("  %-10s %-20s  %7s  %7s%n",
                "ID", "Name", "Present", "Pct(%)");
        System.out.println("  " + LINE);
    }

    public void success(String msg) { System.out.println("\n  ✔ " + msg); }
    public void error(String msg)   { System.out.println("\n  ✘ ERROR: " + msg); }
    public void info(String msg)    { System.out.println("\n  ℹ " + msg); }
    public void warn(String msg)    { System.out.println("\n  ⚠ WARNING: " + msg); }

    public Status readStatus(InputHelper input) {
        int choice = input.readInt("  Enter status (1 = Present, 2 = Absent): ", 1, 2);
        return choice == 1 ? Status.PRESENT : Status.ABSENT;
    }
}

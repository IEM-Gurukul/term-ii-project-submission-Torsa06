package ui;

import exception.DuplicateAttendanceRecordException;
import exception.DuplicateStudentException;
import exception.StudentNotFoundException;
import model.AttendanceRecord;
import model.AttendanceRecord.Status;
import model.Student;
import service.AttendanceManager;
import service.FileHandler;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String DATA_DIR = "data";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InputHelper   input   = new InputHelper(scanner);
        ConsoleUI     ui      = new ConsoleUI();
        AttendanceManager mgr = AttendanceManager.getInstance();
        FileHandler   fh      = new FileHandler(DATA_DIR);

        try {
            mgr.init(fh);
        } catch (IOException e) {
            ui.error("Could not load data: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            ui.showMainMenu();
            int choice = input.readInt("  Select option: ", 0, 3);
            switch (choice) {
                case 1:
                    handleStudentMenu(input, ui, mgr);
                    break;
                case 2:
                    handleAttendanceMenu(input, ui, mgr);
                    break;
                case 3:
                    handleReportsMenu(input, ui, mgr);
                    break;
                case 0:
                    running = false;
                    break;
            }
        }

        System.out.println("\n  Goodbye! Data saved to '" + DATA_DIR + "' directory.");
        scanner.close();
    }

    private static void handleStudentMenu(InputHelper input, ConsoleUI ui,
                                          AttendanceManager mgr) {
        boolean back = false;
        while (!back) {
            ui.showStudentMenu();
            int choice = input.readInt("  Select option: ", 0, 5);
            switch (choice) {
                case 1:
                    registerStudent(input, ui, mgr);
                    break;
                case 2:
                    ui.printStudentList(mgr.getAllStudents());
                    break;
                case 3:
                    searchById(input, ui, mgr);
                    break;
                case 4:
                    searchByName(input, ui, mgr);
                    break;
                case 5:
                    removeStudent(input, ui, mgr);
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    private static void registerStudent(InputHelper input, ConsoleUI ui,
                                        AttendanceManager mgr) {
        System.out.println("\n  ── Register New Student ──");
        String id     = input.readString("  Student ID   : ");
        String name   = input.readString("  Full Name    : ");
        String email  = input.readString("  Email        : ");
        String course = input.readString("  Course       : ");

        try {
            mgr.registerStudent(new Student(id, name, email, course));
            ui.success("Student '" + name + "' registered successfully.");
        } catch (DuplicateStudentException e) {
            ui.error(e.getMessage());
        } catch (IOException e) {
            ui.error("Could not save: " + e.getMessage());
        }
    }

    private static void searchById(InputHelper input, ConsoleUI ui,
                                   AttendanceManager mgr) {
        String id = input.readString("  Enter Student ID: ");
        try {
            ui.printStudent(mgr.getStudentById(id));
        } catch (StudentNotFoundException e) {
            ui.error(e.getMessage());
        }
    }

    private static void searchByName(InputHelper input, ConsoleUI ui,
                                     AttendanceManager mgr) {
        String query = input.readString("  Enter name (or partial name): ");
        List<Student> results = mgr.searchByName(query);
        if (results.isEmpty()) {
            ui.info("No students matched '" + query + "'.");
        } else {
            ui.printStudentList(results);
        }
    }

    private static void removeStudent(InputHelper input, ConsoleUI ui,
                                      AttendanceManager mgr) {
        String id = input.readString("  Enter Student ID to remove: ");
        try {
            Student s = mgr.getStudentById(id);
            ui.printStudent(s);
            if (input.readConfirm("  Remove this student and all their records?")) {
                mgr.removeStudent(id);
                ui.success("Student removed.");
            } else {
                ui.info("Cancelled.");
            }
        } catch (StudentNotFoundException e) {
            ui.error(e.getMessage());
        } catch (IOException e) {
            ui.error("Could not save: " + e.getMessage());
        }
    }
    private static void handleAttendanceMenu(InputHelper input, ConsoleUI ui,
                                             AttendanceManager mgr) {
        boolean back = false;
        while (!back) {
            ui.showAttendanceMenu();
            int choice = input.readInt("  Select option: ", 0, 5);
            switch (choice) {
                case 1:
                    markSingle(input, ui, mgr);
                    break;
                case 2:
                    markAll(input, ui, mgr);
                    break;
                case 3:
                    updateRecord(input, ui, mgr);
                    break;
                case 4:
                    viewHistory(input, ui, mgr);
                    break;
                case 5:
                    viewByDate(input, ui, mgr);
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    private static void markSingle(InputHelper input, ConsoleUI ui,
                                   AttendanceManager mgr) {
        String id = input.readString("  Student ID : ");
        LocalDate date = input.readDate("  Date");
        Status status  = ui.readStatus(input);
        try {
            mgr.markAttendance(id, date, status);
            ui.success("Attendance marked: " + id + " – " + status + " on " + date);
        } catch (StudentNotFoundException | DuplicateAttendanceRecordException e) {
            ui.error(e.getMessage());
        } catch (IOException e) {
            ui.error("Could not save: " + e.getMessage());
        }
    }

    private static void markAll(InputHelper input, ConsoleUI ui,
                                AttendanceManager mgr) {
        List<Student> all = mgr.getAllStudents();
        if (all.isEmpty()) { ui.info("No students registered."); return; }

        LocalDate date = LocalDate.now();
        ui.info("Marking attendance for " + all.size() + " student(s) on " + date);
        int marked = 0;
        for (Student s : all) {
            System.out.print("  " + s.getName() + " (" + s.getId() + ") ");
            Status status = ui.readStatus(input);
            try {
                mgr.markAttendance(s.getId(), date, status);
                marked++;
            } catch (DuplicateAttendanceRecordException e) {
                ui.warn("Already marked for " + s.getId() + " – skipping.");
            } catch (StudentNotFoundException | IOException e) {
                ui.error(e.getMessage());
            }
        }
        ui.success("Marked " + marked + "/" + all.size() + " students.");
    }

    private static void updateRecord(InputHelper input, ConsoleUI ui,
                                     AttendanceManager mgr) {
        String id      = input.readString("  Student ID : ");
        LocalDate date = input.readDate("  Date to update");
        Status status  = ui.readStatus(input);
        try {
            mgr.updateAttendance(id, date, status);
            ui.success("Record updated.");
        } catch (StudentNotFoundException e) {
            ui.error(e.getMessage());
        } catch (IOException e) {
            ui.error("Could not save: " + e.getMessage());
        }
    }

    private static void viewHistory(InputHelper input, ConsoleUI ui,
                                    AttendanceManager mgr) {
        String id = input.readString("  Student ID : ");
        try {
            Student s = mgr.getStudentById(id);
            ui.printStudent(s);
            List<AttendanceRecord> history = mgr.getAttendanceHistory(id);
            ui.printAttendanceList(history);
            double pct = mgr.getAttendancePercentage(id);
            System.out.printf("%n  Attendance Percentage: %.1f%%%n", pct);
        } catch (StudentNotFoundException e) {
            ui.error(e.getMessage());
        }
    }

    private static void viewByDate(InputHelper input, ConsoleUI ui,
                                   AttendanceManager mgr) {
        LocalDate date = input.readDate("  Date");
        List<AttendanceRecord> recs = mgr.getRecordsByDate(date);
        ui.info("Records for " + date + ":");
        ui.printAttendanceList(recs);
    }

    private static void handleReportsMenu(InputHelper input, ConsoleUI ui,
                                          AttendanceManager mgr) {
        boolean back = false;
        while (!back) {
            ui.showReportsMenu();
            int choice = input.readInt("  Select option: ", 0, 3);
            switch (choice) {
                case 1:
                    reportSingle(input, ui, mgr);
                    break;
                case 2:
                    reportAll(ui, mgr);
                    break;
                case 3:
                    reportLowAttendance(input, ui, mgr);
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    private static void reportSingle(InputHelper input, ConsoleUI ui,
                                     AttendanceManager mgr) {
        String id = input.readString("  Student ID: ");
        try {
            Student s  = mgr.getStudentById(id);
            double pct = mgr.getAttendancePercentage(id);
            List<AttendanceRecord> history = mgr.getAttendanceHistory(id);
            long present = history.stream().filter(AttendanceRecord::isPresent).count();
            ui.printStudent(s);
            System.out.printf("  Attendance: %d/%d sessions  →  %.1f%%%n",
                    (int) present, history.size(), pct);
        } catch (StudentNotFoundException e) {
            ui.error(e.getMessage());
        }
    }

    private static void reportAll(ConsoleUI ui, AttendanceManager mgr) {
        List<Student> students = mgr.getAllStudents();
        if (students.isEmpty()) { ui.info("No students registered."); return; }
        ui.printSummaryHeader();
        for (Student s : students) {
            try {
                double pct = mgr.getAttendancePercentage(s.getId());
                List<AttendanceRecord> history = mgr.getAttendanceHistory(s.getId());
                long present = history.stream().filter(AttendanceRecord::isPresent).count();
                ui.printAttendanceSummary(s.getId(), s.getName(), pct,
                        history.size(), (int) present);
            } catch (StudentNotFoundException ignored) {}
        }
    }

    private static void reportLowAttendance(InputHelper input, ConsoleUI ui,
                                            AttendanceManager mgr) {
        int threshold = input.readInt("  Enter threshold % (e.g. 75): ", 0, 100);
        List<Student> low = mgr.getLowAttendanceStudents(threshold);
        if (low.isEmpty()) {
            ui.info("No students below " + threshold + "% attendance.");
        } else {
            ui.warn(low.size() + " student(s) below " + threshold + "% attendance:");
            ui.printSummaryHeader();
            for (Student s : low) {
                try {
                    double pct = mgr.getAttendancePercentage(s.getId());
                    List<AttendanceRecord> history = mgr.getAttendanceHistory(s.getId());
                    long present = history.stream().filter(AttendanceRecord::isPresent).count();
                    ui.printAttendanceSummary(s.getId(), s.getName(), pct,
                            history.size(), (int) present);
                } catch (StudentNotFoundException ignored) {}
            }
        }
    }
}

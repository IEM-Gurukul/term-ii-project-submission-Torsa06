package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class AttendanceRecord {

    public enum Status { PRESENT, ABSENT }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String    studentId;
    private LocalDate date;
    private Status    status;

    public AttendanceRecord(String studentId, LocalDate date, Status status) {
        this.studentId = studentId;
        this.date      = date;
        this.status    = status;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String    getStudentId() { return studentId; }
    public LocalDate getDate()      { return date; }
    public Status    getStatus()    { return status; }

    public boolean isPresent() { return status == Status.PRESENT; }

    public String getDetails() {
        return String.format("[%s] Student %-10s – %s",
                date.format(FMT), studentId, status);
    }

    public String toCsvLine() {
        return studentId + "," + date.format(FMT) + "," + status.name();
    }


    public static AttendanceRecord fromCsvLine(String line) {
        String[] p = line.split(",", 3);
        if (p.length != 3) {
            throw new IllegalArgumentException("Malformed attendance CSV line: " + line);
        }
        return new AttendanceRecord(
                p[0].trim(),
                LocalDate.parse(p[1].trim(), FMT),
                Status.valueOf(p[2].trim().toUpperCase())
        );
    }

    @Override
    public String toString() { return getDetails(); }
}

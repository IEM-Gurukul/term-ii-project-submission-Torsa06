package service;

import model.AttendanceRecord;
import model.Student;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private final String studentsFile;
    private final String attendanceFile;

    public FileHandler(String dataDirectory) {
        try {
            Files.createDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory – " + e.getMessage());
        }
        this.studentsFile   = dataDirectory + File.separator + "students.csv";
        this.attendanceFile = dataDirectory + File.separator + "attendance.csv";
    }

    public void saveAllStudents(List<Student> students) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(studentsFile))) {
            bw.write("studentId,name,email,course");
            bw.newLine();
            for (Student s : students) {
                bw.write(s.toCsvLine());
                bw.newLine();
            }
        }
    }

    public List<Student> loadAllStudents() throws IOException {
        List<Student> students = new ArrayList<>();
        File file = new File(studentsFile);
        if (!file.exists()) return students;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                line = line.trim();
                if (!line.isEmpty()) {
                    students.add(Student.fromCsvLine(line));
                }
            }
        }
        return students;
    }

    public void saveAllRecords(List<AttendanceRecord> records) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(attendanceFile))) {
            bw.write("studentId,date,status");
            bw.newLine();
            for (AttendanceRecord r : records) {
                bw.write(r.toCsvLine());
                bw.newLine();
            }
        }
    }

    public List<AttendanceRecord> loadAllRecords() throws IOException {
        List<AttendanceRecord> records = new ArrayList<>();
        File file = new File(attendanceFile);
        if (!file.exists()) return records;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                line = line.trim();
                if (!line.isEmpty()) {
                    records.add(AttendanceRecord.fromCsvLine(line));
                }
            }
        }
        return records;
    }

    public String getStudentsFilePath()   { return studentsFile; }
    public String getAttendanceFilePath() { return attendanceFile; }
}

package service;

import exception.DuplicateAttendanceRecordException;
import exception.DuplicateStudentException;
import exception.StudentNotFoundException;
import model.AttendanceRecord;
import model.AttendanceRecord.Status;
import model.Student;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AttendanceManager {
    private static AttendanceManager instance;
    private AttendanceManager() {}

    public static AttendanceManager getInstance() {
        if (instance == null) {
            instance = new AttendanceManager();
        }
        return instance;
    }
    private final Map<String, Student> studentMap = new HashMap<>();
    private final List<AttendanceRecord> records = new ArrayList<>();
    private FileHandler fileHandler;

    public void init(FileHandler fh) throws IOException {
        this.fileHandler = fh;
        for (Student s : fh.loadAllStudents()) {
            studentMap.put(s.getId(), s);
        }
        records.addAll(fh.loadAllRecords());
        System.out.println("Loaded " + studentMap.size() + " student(s) and "
                + records.size() + " attendance record(s) from disk.");
    }

    public void registerStudent(Student student) throws DuplicateStudentException, IOException {
        if (studentMap.containsKey(student.getId())) {
            throw new DuplicateStudentException(student.getId());
        }
        studentMap.put(student.getId(), student);
        persist();
    }

    public void removeStudent(String studentId) throws StudentNotFoundException, IOException {
        ensureExists(studentId);
        studentMap.remove(studentId);
        records.removeIf(r -> r.getStudentId().equals(studentId));
        persist();
    }

    public Student getStudentById(String studentId) throws StudentNotFoundException {
        ensureExists(studentId);
        return studentMap.get(studentId);
    }

    public List<Student> getAllStudents() {
        return studentMap.values().stream()
                .sorted(Comparator.comparing(Student::getId))
                .collect(Collectors.toList());
    }

    public List<Student> searchByName(String query) {
        String lower = query.toLowerCase();
        return studentMap.values().stream()
                .filter(s -> s.getName().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(Student::getName))
                .collect(Collectors.toList());
    }

    public void markAttendance(String studentId, LocalDate date, Status status)
            throws StudentNotFoundException, DuplicateAttendanceRecordException, IOException {
        ensureExists(studentId);
        boolean duplicate = records.stream()
                .anyMatch(r -> r.getStudentId().equals(studentId) && r.getDate().equals(date));
        if (duplicate) {
            throw new DuplicateAttendanceRecordException(studentId, date);
        }
        records.add(new AttendanceRecord(studentId, date, status));
        persist();
    }

    public void updateAttendance(String studentId, LocalDate date, Status newStatus)
            throws StudentNotFoundException, IOException {
        ensureExists(studentId);
        boolean updated = false;
        for (int i = 0; i < records.size(); i++) {
            AttendanceRecord r = records.get(i);
            if (r.getStudentId().equals(studentId) && r.getDate().equals(date)) {
                records.set(i, new AttendanceRecord(studentId, date, newStatus));
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new StudentNotFoundException(
                    studentId + " (no record for " + date + ")");
        }
        persist();
    }

    public List<AttendanceRecord> getAttendanceHistory(String studentId)
            throws StudentNotFoundException {
        ensureExists(studentId);
        return records.stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .sorted(Comparator.comparing(AttendanceRecord::getDate))
                .collect(Collectors.toList());
    }

    public double getAttendancePercentage(String studentId) throws StudentNotFoundException {
        ensureExists(studentId);
        List<AttendanceRecord> history = records.stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
        if (history.isEmpty()) return 0.0;
        long presentCount = history.stream().filter(AttendanceRecord::isPresent).count();
        return (presentCount * 100.0) / history.size();
    }

    public List<Student> getLowAttendanceStudents(double threshold) {
        List<Student> result = new ArrayList<>();
        for (Student s : studentMap.values()) {
            try {
                double pct = getAttendancePercentage(s.getId());
                if (pct < threshold) result.add(s);
            } catch (StudentNotFoundException ignored) { /* won't happen */ }
        }
        result.sort(Comparator.comparing(Student::getId));
        return result;
    }

    public List<AttendanceRecord> getRecordsByDate(LocalDate date) {
        return records.stream()
                .filter(r -> r.getDate().equals(date))
                .sorted(Comparator.comparing(AttendanceRecord::getStudentId))
                .collect(Collectors.toList());
    }

    private void ensureExists(String studentId) throws StudentNotFoundException {
        if (!studentMap.containsKey(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
    }

    private void persist() throws IOException {
        if (fileHandler != null) {
            fileHandler.saveAllStudents(new ArrayList<>(studentMap.values()));
            fileHandler.saveAllRecords(records);
        }
    }
    public int getTotalStudents()  { return studentMap.size(); }
    public int getTotalRecords()   { return records.size(); }
}

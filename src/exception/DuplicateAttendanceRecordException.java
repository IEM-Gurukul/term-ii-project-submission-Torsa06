package exception;

import java.time.LocalDate;

public class DuplicateAttendanceRecordException extends Exception {

    public DuplicateAttendanceRecordException(String studentId, LocalDate date) {
        super("Attendance for student '" + studentId + "' on " + date + " is already recorded.");
    }
}

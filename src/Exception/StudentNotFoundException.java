package exception;
public class StudentNotFoundException extends Exception {

    public StudentNotFoundException(String studentId) {
        super("No student found with ID: " + studentId);
    }
}

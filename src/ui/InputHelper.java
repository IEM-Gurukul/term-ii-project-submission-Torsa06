package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHelper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readString(String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) System.out.println("  Input cannot be empty. Try again.");
        } while (value.isEmpty());
        return value;
    }
    public String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
                System.out.printf("  Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Try again.");
            }
        }
    }
    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " [yyyy-MM-dd, blank = today]: ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) return LocalDate.now();
            try {
                return LocalDate.parse(line, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }
    public boolean readConfirm(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String line = scanner.nextLine().trim().toLowerCase();
        return line.equals("y") || line.equals("yes");
    }
}

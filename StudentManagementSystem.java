import java.sql.*;
import java.util.Scanner;


public class StudentManagementSystem {

   
    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

   
    private static void addStudent(Scanner sc) {
        System.out.print("Name   : ");
        String name = sc.nextLine().trim();
        System.out.print("Email  : ");
        String email = sc.nextLine().trim();
        System.out.print("Course : ");
        String course = sc.nextLine().trim();
        System.out.print("Age    : ");
        int age;
        try {
            age = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Student not added.");
            return;
        }

        if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
            System.out.println("All fields are required. Student not added.");
            return;
        }

        String sql = "INSERT INTO students (name, email, course, age) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, course);
            ps.setInt(4, age);
            ps.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("A student with this email already exists.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

   
    private static void viewStudents() {
        String sql = "SELECT id, name, email, course, age FROM students ORDER BY id";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println();
            System.out.printf("%-5s %-20s %-28s %-18s %-4s%n", "ID", "NAME", "EMAIL", "COURSE", "AGE");
            System.out.println("-".repeat(80));

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-20s %-28s %-18s %-4d%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("course"),
                        rs.getInt("age"));
            }
            if (!found) {
                System.out.println("No student records found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
    }

    
    private static void deleteStudent(Scanner sc) {
        System.out.print("Enter student ID to delete: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("No student found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }

    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Delete Student");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> addStudent(sc);
                case "2" -> viewStudents();
                case "3" -> deleteStudent(sc);
                case "4" -> {
                    System.out.println("Goodbye!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter 1-4.");
            }
        }
    }
}

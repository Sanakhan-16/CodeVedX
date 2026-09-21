import java.sql.*;
import java.util.Scanner;

/**
 * Library Management System (Intermediate)
 * Features: Add books, Issue and return books, View available books
 * Tech: Java, JDBC, MySQL
 */
public class LibraryManagementSystem {

    // ---- Change these to match your MySQL setup ----
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ---------- ADD BOOK ----------
    private static void addBook(Scanner sc) {
        System.out.print("Title    : ");
        String title = sc.nextLine().trim();
        System.out.print("Author   : ");
        String author = sc.nextLine().trim();
        System.out.print("Copies   : ");
        int copies;
        try {
            copies = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of copies.");
            return;
        }
        if (title.isEmpty() || author.isEmpty() || copies <= 0) {
            System.out.println("Title, author and a positive number of copies are required.");
            return;
        }

        String sql = "INSERT INTO books (title, author, total_copies, available_copies) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, copies);
            ps.setInt(4, copies);
            ps.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    // ---------- VIEW AVAILABLE BOOKS ----------
    private static void viewAvailableBooks() {
        String sql = "SELECT id, title, author, available_copies, total_copies "
                   + "FROM books WHERE available_copies > 0 ORDER BY id";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println();
            System.out.printf("%-5s %-30s %-22s %-10s%n", "ID", "TITLE", "AUTHOR", "AVAILABLE");
            System.out.println("-".repeat(72));

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-30s %-22s %d / %d%n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("available_copies"),
                        rs.getInt("total_copies"));
            }
            if (!found) {
                System.out.println("No books are currently available.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
    }

    // ---------- ISSUE BOOK ----------
    private static void issueBook(Scanner sc) {
        System.out.print("Book ID     : ");
        int bookId;
        try {
            bookId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid book ID.");
            return;
        }
        System.out.print("Member name : ");
        String member = sc.nextLine().trim();
        if (member.isEmpty()) {
            System.out.println("Member name is required.");
            return;
        }

        String updateSql = "UPDATE books SET available_copies = available_copies - 1 "
                         + "WHERE id = ? AND available_copies > 0";
        String insertSql = "INSERT INTO issue_records (book_id, member_name, issue_date) "
                         + "VALUES (?, ?, CURDATE())";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false); // both steps succeed or neither does
            try (PreparedStatement upd = con.prepareStatement(updateSql);
                 PreparedStatement ins = con.prepareStatement(insertSql)) {

                upd.setInt(1, bookId);
                if (upd.executeUpdate() == 0) {
                    con.rollback();
                    System.out.println("Book not found or no copies available.");
                    return;
                }
                ins.setInt(1, bookId);
                ins.setString(2, member);
                ins.executeUpdate();
                con.commit();
                System.out.println("Book issued to " + member + ".");
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    // ---------- RETURN BOOK ----------
    private static void returnBook(Scanner sc) {
        System.out.print("Book ID     : ");
        int bookId;
        try {
            bookId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid book ID.");
            return;
        }
        System.out.print("Member name : ");
        String member = sc.nextLine().trim();

        String updRecord = "UPDATE issue_records SET return_date = CURDATE() "
                         + "WHERE book_id = ? AND member_name = ? AND return_date IS NULL LIMIT 1";
        String updBook = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement rec = con.prepareStatement(updRecord);
                 PreparedStatement bk = con.prepareStatement(updBook)) {

                rec.setInt(1, bookId);
                rec.setString(2, member);
                if (rec.executeUpdate() == 0) {
                    con.rollback();
                    System.out.println("No active issue record found for this book and member.");
                    return;
                }
                bk.setInt(1, bookId);
                bk.executeUpdate();
                con.commit();
                System.out.println("Book returned successfully.");
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    // ---------- VIEW ISSUED BOOKS ----------
    private static void viewIssuedBooks() {
        String sql = "SELECT r.id, b.title, r.member_name, r.issue_date "
                   + "FROM issue_records r JOIN books b ON r.book_id = b.id "
                   + "WHERE r.return_date IS NULL ORDER BY r.id";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println();
            System.out.printf("%-5s %-30s %-20s %-12s%n", "ID", "TITLE", "MEMBER", "ISSUED ON");
            System.out.println("-".repeat(70));

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-30s %-20s %-12s%n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("member_name"),
                        rs.getDate("issue_date"));
            }
            if (!found) {
                System.out.println("No books are currently issued.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching issued books: " + e.getMessage());
        }
    }

    // ---------- MAIN MENU ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Book");
            System.out.println("2. View Available Books");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. View Issued Books");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> addBook(sc);
                case "2" -> viewAvailableBooks();
                case "3" -> issueBook(sc);
                case "4" -> returnBook(sc);
                case "5" -> viewIssuedBooks();
                case "6" -> {
                    System.out.println("Goodbye!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter 1-6.");
            }
        }
    }
}

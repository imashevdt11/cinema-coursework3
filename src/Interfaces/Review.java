package Interfaces;

import Other.MyConnection;
import Accounts.*;

import java.io.IOException;
import java.util.Scanner;
import java.sql.*;

public interface Review {
    static void replyReview() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nENTER THE REVIEW ID: ");
        String reviewID = scanner.nextLine();
        System.out.print("REPLY (MUST CONTAIN LESS THAN 80 CHARACTERS): ");
        String reply = scanner.nextLine();
        if (reply.length() > 80) {
            System.out.println("""
                    \nYOU HAVE ENTERED THE NUMBER OF CHARACTERS EXCEEDING THE SPECIFIED LIMIT
                                        
                    DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
            String choice = scanner.nextLine();
            if (choice.equals("1")) replyReview();
            else Admin.aMenu();
        }
        ResultSet resultSet = MyConnection.statement.executeQuery("SELECT * FROM admins " +
                "WHERE firstName = '" + Admin.getFirstName() + "' AND lastName = '" +  Admin.getLastName() + "';");
        String adminID = null;
        while (resultSet.next()) {
            adminID = resultSet.getString(1);
        }
        MyConnection.statement.executeUpdate("UPDATE reviews SET reply = '" + reply + "' WHERE reviewID =  " + reviewID + ";");
        MyConnection.statement.executeUpdate("UPDATE reviews SET adminID = " + adminID + " WHERE reviewID =  " + reviewID + ";");
        System.out.println("\nDATA SAVED");
        Admin.aMenu();
    }
    static void addReview() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nENTER THE REVIEW (MUST CONTAIN LESS THAN 80 CHARACTERS): ");
        String review = scanner.nextLine();
        if (review.length() > 80) {
            System.out.println("""
                    \nYOU HAVE ENTERED THE NUMBER OF CHARACTERS EXCEEDING THE SPECIFIED LIMIT
                                        
                    DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
            String choice = scanner.nextLine();
            if (choice.equals("1")) addReview();
            else Visitor.vMenu();
        }
        ResultSet resultSet = MyConnection.statement.executeQuery("" +
                "SELECT * FROM visitors WHERE password = '" + Visitor.getPassword() + "';");
        String visitorResult = null;
        if (resultSet.next()) visitorResult = resultSet.getString(1);
        String visitorID = visitorResult;
        MyConnection.statement.executeUpdate("INSERT INTO reviews(visitorID, review, dateOfReview) " +
                "VALUES(" + visitorID + ", '" + review + "', now());");
        System.out.println("\nREVIEW HAS BEEN ADDED");
        Visitor.vMenu();
    }
    static void getReviews() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet = MyConnection.statement.executeQuery(
                "SELECT v.firstname, v.lastname, r.reviewID, r.review, r.dateOfReview FROM reviews r, visitors v " +
                        "WHERE v.visitorID = r.visitorID ORDER BY r.dateOfReview");
        System.out.printf("\n%-10s%-45s%-80s%-30s%n", "REVIEW ID", "VISITOR", "REVIEW", "DATE OF REVIEW");
        System.out.println();
        while (resultSet.next()) {
            System.out.printf("%-10s", resultSet.getString(3));
            System.out.printf("%-45s", resultSet.getString(1) + " " + resultSet.getString(2));
            System.out.printf("%-80s", resultSet.getString(4));
            System.out.printf("%-30s", resultSet.getString(5));
            System.out.println();}
        System.out.print("\nDO YOU WANT TO GET REPLY FOR REVIEW? (1 - YES / 0 - NO): ");
        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            System.out.print("ENTER THE REVIEW ID: ");
            choice = scanner.nextLine();
            ResultSet resultSet2 = MyConnection.statement.executeQuery(
                    "SELECT a.firstName, a.lastName, r.reply FROM reviews r, admins a " +
                            "WHERE r.adminID = a.adminID AND reviewID = " + choice + ";");
            System.out.printf("\n%-45s%-80s%n", "ADMIN", "REPLY");
            while (resultSet2.next()) {
                System.out.printf("%-45s", resultSet2.getString(1) + " " + resultSet2.getString(2));
                System.out.printf("%-80s", resultSet2.getString(3));
                System.out.println();
            }
        }
    }
}




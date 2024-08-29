package Accounts;

import Other.MyConnection;
import Interfaces.*;

import java.io.IOException;
import java.util.Scanner;
import java.sql.*;

public class Admin extends User {
    public static void aMenu() throws ClassNotFoundException, SQLException, IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————");
        System.out.print("""
                \nENTER THE NUMBER OF MENU'S OPTION

                1 - ASSIGNMENTS
                2 - SCHEDULE
                3 - REVIEWS
                4 - MOVIES
                
                5 - COMPLETE ASSIGNMENT
                6 - REPLY TO A REVIEW
                7 - FIND VISITOR
                8 - ADD SESSION
                9 - ADD MOVIE

                10 - LOG OUT OF ACCOUNT
                0 - SHUT DOWN THE PROGRAMME:\040""");
        String choice = scanner.nextLine();
        System.out.println("\n———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————");

        switch (choice) {

            case "1" -> {
                Assignment.getAssignments();
                aMenu();
            }
            case "2" -> {
                Session.getSchedule();
                aMenu();
            }
            case "3" -> {
                Review.getReviews();
                aMenu();
            }
            case "4" -> {
                Movie.getMovies();
                aMenu();
            }

            case "5" -> Assignment.completeAssignment();
            case "6" -> Review.replyReview();
            case "7" -> {
                Visitor.findVisitor();
                aMenu();
            }
            case "8" -> Session.addSession();
            case "9" -> Movie.addMovie();

            case "10" -> typeChoosing();
            case "0" -> System.out.print("\nGOODBYE! HAVE A NICE DAY!\n");
            default -> {
                System.out.println("\nTHE ENTERED MENU NUMBER IS INVALID");
                aMenu();
            }
        }
    }
    static void removeAdmin() throws IOException, ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nFIRST NAME: ");
        String firstName = scanner.nextLine();
        System.out.print("LAST NAME: ");
        String lastName = scanner.nextLine();
        System.out.print("PASSWORD: ");
        String password = scanner.nextLine();

        MyConnection.statement.executeUpdate("UPDATE admins SET status = 'former' " +
                "WHERE firstname = '" + firstName + "' AND lastname = '" + lastName + "' AND password = '" + password + "';");
        MyConnection.statement.executeUpdate("UPDATE admins SET dateofdismissal = now() " +
                "WHERE firstname = '" + firstName + "' AND lastname = '" + lastName + "' AND password = '" + password + "';");

        System.out.println("\n" + firstName + " " + lastName + "'s DATA CHANGED");
        Manager.mMenu();
    }
    static void findAdmin() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nFIRST NAME: ");
        String firstName = scanner.nextLine();
        System.out.print("SECOND NAME: ");
        String lastName = scanner.nextLine();

        ResultSet resultSet = MyConnection.statement.executeQuery("SELECT * FROM admins " +
                "WHERE firstName = '" + firstName + "' AND lastName = '" +  lastName + "';");

        System.out.printf("\n%-25s%-25s%-25s%-15s%-35s%n", "FIRST NAME ", "LAST NAME", "PHONE NUMBER", "STATUS", "NUMBER OF COMPLETE ASSIGNMENTS");
        System.out.println();
        while (resultSet.next()) {
            System.out.printf("%-25s", resultSet.getString(2));
            System.out.printf("%-25s", resultSet.getString(3));
            System.out.printf("%-25s", resultSet.getString(4));
            System.out.printf("%-15s", resultSet.getString(6));
            System.out.printf("%-35s", resultSet.getString(7));
            System.out.println();
        }
        Manager.mMenu();
    }
    static void addAdmin() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nFIRST NAME (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
        String firstName = scanner.nextLine();
        System.out.print("LAST NAME (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
        String lastName = scanner.nextLine();
        System.out.print("PHONE NUMBER (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
        String phoneNumber = scanner.nextLine();
        System.out.print("PASSWORD (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
        String password = scanner.nextLine();
        if (firstName.length() > 20 || lastName.length() > 20 || phoneNumber.length() > 20 || password.length() > 20) {
            System.out.print("""
                    \nYOU HAVE ENTERED THE NUMBER OF CHARACTERS EXCEEDING THE SPECIFIED LIMIT
                    
                    DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
            String choice = scanner.nextLine();
            if (choice.equals("1")) addAdmin();
            else Manager.mMenu();
        }
        else {
            Admin.setFirstName(firstName);
            Admin.setLastName(lastName);
            Admin.setPassword(password);
            boolean isUserExists = false;
            PreparedStatement preparedStatement = MyConnection.connection.prepareStatement(
                    "SELECT password FROM admins WHERE password = ?"); {
                preparedStatement.setString(1, password);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) { if (resultSet.next()) isUserExists = true; }
            if (isUserExists) {
                System.out.println("""
                        \nADMIN WITH ENTERED DATA ALREADY EXISTS
                        
                        DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
                String choice = scanner.nextLine();
                if (choice.equals("1")) addAdmin();
                else Manager.mMenu();
            } else {
                MyConnection.statement.executeUpdate("INSERT INTO admins(firstname, lastname, phoneNumber, password) " +
                        "VALUES ('" + firstName + "', '" + lastName + "', '" + phoneNumber + "', '" + password + "');");
                System.out.println("\n" + firstName + " " + lastName + "'s DATA IS STORED IN THE DATABASE");
                Manager.mMenu();
            }
        }
    }
    static void aLogIn() throws ClassNotFoundException, SQLException, IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.print("\nFIRST NAME: ");
        String firstName = scanner.nextLine();
        System.out.print("LAST NAME: ");
        String lastName = scanner.nextLine();
        System.out.print("PASSWORD: ");
        String password = scanner.nextLine();

        Admin.setFirstName(firstName);
        Admin.setLastName(lastName);
        Admin.setPassword(password);

        boolean isUserExists = false;
        PreparedStatement preparedStatement = MyConnection.connection.prepareStatement("SELECT firstName, lastName, status, password " +
                "FROM admins WHERE firstName = ? AND lastName = ? AND status = 'current' AND password = ?");
        {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) isUserExists = true;
            }
        }

        if (isUserExists) aMenu();
        else {
            System.out.print("""
                    \nNO DATA FOUND

                    DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
            String choice = scanner.nextLine();

            if (choice.equals("1")) aLogIn();
            else typeChoosing();
        }
    }
}


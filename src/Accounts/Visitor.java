package Accounts;

import Other.MyConnection;
import Interfaces.*;
import java.util.*;
import java.sql.*;
import java.io.*;

public class Visitor extends User {
    static void replenishTheBalance() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nENTER THE AMOUNT OF MONEY YOU WANT TO REPLENISH YOUR BALANCE WITH: ");
        int amountOfMoney = scanner.nextInt();

        MyConnection.statement.executeUpdate("UPDATE visitors SET balance = (balance + " + amountOfMoney + ") " +
                "WHERE password = '" + Visitor.getPassword() + "';");

        System.out.println("\nDATA SAVED");
        Visitor.vMenu();
    }
    static void getUsersStatistic() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nENTER THE TIME PERIOD FOR WHICH YOU WANT TO RECEIVE INFORMATION ABOUT REGISTERED AND UNREGISTERED USERS");
        System.out.print("\nFROM (FOR EXAMPLE, 2022-12-07): ");
        String start = scanner.nextLine();
        System.out.print("TO (FOR EXAMPLE, 2022-12-21): ");
        String end = scanner.nextLine();
        String queryTime = "'" + start + " 00:00:00' AND '" + end + " 23:59:59'";

        int registered = 0;
        int unregistered = 0;
        ResultSet resultSet = MyConnection.statement.executeQuery("SELECT COUNT(*) FROM visitors " +
                "WHERE status = 'current' AND registrationDate BETWEEN " + queryTime + ";");
        while (resultSet.next()) registered = resultSet.getInt(1);
        ResultSet resultSet2 = MyConnection.statement.executeQuery("SELECT COUNT(*) FROM visitors " +
                "WHERE status = 'former' AND deletionDate BETWEEN " + queryTime + ";");
        while (resultSet2.next()) unregistered = resultSet2.getInt(1);

        System.out.print("\nTHE RATION OF REGISTERED/UNREGISTERED USERS FROM " + start + " TO " + end + ": " + registered + "/" + unregistered);
        Manager.mMenu();
    }
    static void deleteAccount() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nARE YOU SURE YOU WANT TO DELETE YOUR ACCOUNT? (1 - YES / 0 - NO): ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            MyConnection.statement.executeUpdate("UPDATE visitors SET status = 'former' " +
                    "WHERE firstname = '" + Visitor.getFirstName() + "' AND lastname = '" + Visitor.getLastName() + "' AND password = '"
                    + Visitor.getPassword() + "';");
            MyConnection.statement.executeUpdate("UPDATE visitors SET deletionDate = now() " +
                    "WHERE firstname = '" + Visitor.getFirstName() + "' AND lastname = '" + Visitor.getLastName() + "' AND password = '" +
                    Visitor.getPassword() + "';");
            System.out.println("\nYOUR ACCOUNT HAS BEEN DELETED");
            typeChoosing();
        }
        else vMenu();
    }
    public static void vMenu() throws ClassNotFoundException, SQLException, IOException {

        System.out.println("\n———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————");
        System.out.print("""
                \nENTER THE NUMBER OF MENU'S OPTION
                
                1 - SCHEDULE
                2 - REVIEWS
                3 - BALANCE

                4 - BOUGHT TICKETS
                5 - FIND TICKET
                6 - BUY TICKET
                
                7 - REPLENISH THE BALANCE
                8 - DELETE ACCOUNT
                9 - ADD REVIEW

                10 - LOG OUT OF ACCOUNT
                0 - SHUT DOWN THE PROGRAMME:\040""");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        System.out.println("\n———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————");

        switch (choice) {

            case "1" -> {
                Session.getSchedule();
                vMenu();
            }
            case "2" -> {
                Review.getReviews();
                vMenu();
            }
            case "3" -> getBalance();
            case "4" -> Ticket.getBoughtTickets();
            case "5" -> Ticket.findTicket();
            case "6" -> Ticket.buyTicket();
            case "7" -> replenishTheBalance();
            case "8" -> deleteAccount();
            case "9" -> Review.addReview();

            case "10" -> typeChoosing();
            case "0" -> System.out.print("\nGOODBYE! HAVE A NICE DAY!\n");
            default -> {
                System.out.println("\nTHE ENTERED MENU NUMBER IS INVALID");
                vMenu();
            }
        }
    }
    static void getBalance() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        ResultSet resultSet = MyConnection.statement.executeQuery("SELECT balance FROM visitors " +
                "WHERE firstName = '" + Visitor.getFirstName() + "' AND lastName = '" + Visitor.getLastName() + "' AND password = '"  + Visitor.getPassword() + "';");
        int balance = 0;
        while (resultSet.next())  balance = resultSet.getInt(1);

        System.out.print("\n" + Visitor.getFirstName() + " " + Visitor.getLastName() + "'s balance: " + balance);
        Visitor.vMenu();
    }
    static void vLogIn() throws ClassNotFoundException, SQLException, IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.print("\nFIRST NAME: ");
        String firstName = scanner.nextLine();
        System.out.print("LAST NAME: ");
        String lastName = scanner.nextLine();
        System.out.print("PASSWORD: ");
        String password = scanner.nextLine();

        Visitor.setFirstName(firstName);
        Visitor.setLastName(lastName);
        Visitor.setPassword(password);

        boolean isUserExists = false;
        PreparedStatement preparedStatement = MyConnection.connection.prepareStatement("SELECT firstname, lastname, status, password " +
                "FROM visitors WHERE firstName = ? AND lastName = ? AND password = ? AND status = 'current'");
        {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) isUserExists = true;
            }
        }
        if (isUserExists) vMenu();
        else {
            System.out.print("""
                    \nNO DATA FOUND
                    
                    DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");

            String choice = scanner.nextLine();

            if (choice.equals("1")) vLogIn();
            else typeChoosing();
        }
    }
    static void signUp() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scannerInt = new Scanner(System.in);
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("\nFIRST NAME (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
            String firstName = scanner.nextLine();
            System.out.print("LAST NAME (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
            String lastName = scanner.nextLine();
            System.out.print("PHONE NUMBER (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
            String phoneNumber = scanner.nextLine();
            System.out.print("PASSWORD (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
            String password = scanner.nextLine();
            System.out.print("BALANCE (IN KGS): ");
            int balance = scannerInt.nextInt();
            if (firstName.length() > 20 || lastName.length() > 20 || password.length() > 20 || phoneNumber.length() > 20) {
                System.out.print("""
                        \nYOU HAVE ENTERED THE NUMBER OF CHARACTERS EXCEEDING THE SPECIFIED LIMIT
                        
                        DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
                String choice = scanner.nextLine();
                if (choice.equals("1")) signUp();
                else typeChoosing();
            }
            else {
                Visitor.setFirstName(firstName);
                Visitor.setLastName(lastName);
                Visitor.setPassword(password);
                boolean isUserExists = false;
                PreparedStatement prepareStatement = MyConnection.connection.prepareStatement("SELECT password FROM visitors WHERE password = ?");{
                    prepareStatement.setString(1, password);
                    try (ResultSet resultSet = prepareStatement.executeQuery()) {
                        if (resultSet.next()) isUserExists = true;}}
                if (isUserExists) {
                    System.out.print("""
                            \nVISITOR WITH ENTERED DATA ALREADY EXISTS
                            
                            DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
                    String choice = scanner.nextLine();
                    if (choice.equals("1")) signUp();
                    else vMenu();
                } else {
                    MyConnection.statement.executeUpdate("INSERT INTO visitors(firstName, lastName, phoneNumber, password, balance) " +
                            "VALUES ('" + firstName + "', '" + lastName + "', '" + phoneNumber + "', '" + password + "', " + balance + ");");
                    System.out.println("\n" + firstName + " " + lastName + "'s DATA IS STORED IN THE DATABASE");
                    typeChoosing();}
            }
        } catch (InputMismatchException exception) {
            System.out.println("\nPLEASE, ENTER THE BALANCE IN NUMERIC FORMAT");
            signUp();
        }
    }
    static void findVisitor() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nFIRST NAME: ");
        String firstName = scanner.nextLine();
        System.out.print("SECOND NAME: ");
        String lastName = scanner.nextLine();

        ResultSet resultSet = MyConnection.statement.executeQuery("SELECT * FROM visitors " +
                "WHERE firstName = '" + firstName + "' AND lastName = '" +  lastName + "';");

        System.out.printf("\n%-30s%-30s%-30s%-30s%n", "FIRST NAME", "LAST NAME", "PHONE NUMBER", "STATUS");
        System.out.println();
        while (resultSet.next()) {
            System.out.printf("%-30s", resultSet.getString(2));
            System.out.printf("%-30s", resultSet.getString(3));
            System.out.printf("%-30s", resultSet.getString(4));
            System.out.printf("%-30s", resultSet.getString(7));
            System.out.println();
        }
    }
}


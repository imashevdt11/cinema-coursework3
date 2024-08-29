package Interfaces;

import Other.MyConnection;
import Accounts.*;

import java.io.IOException;
import java.util.Scanner;
import java.sql.*;

public interface Ticket {
    static void getIncomeStatement() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nENTER THE TIME PERIOD FOR WHICH YOU WANT TO RECEIVE INCOME STATEMENT");
        System.out.print("\nFROM (FOR EXAMPLE, 2022-12-07): ");
        String start = scanner.nextLine();
        System.out.print("TO (FOR EXAMPLE, 2022-12-21): ");
        String end = scanner.nextLine();
        String queryTime = "'" + start + " 00:00:00' AND '" + end + " 23:59:59'";
        int profit = 0;

        ResultSet resultSet = MyConnection.statement.executeQuery("SELECT SUM(ticketPrice) FROM tickets " +
                "WHERE status = 'sold' AND dateOfPurchase BETWEEN " + queryTime + ";");
        while (resultSet.next()) profit = resultSet.getInt(1);
        System.out.print("\nPROFIT FROM TICKETS SOLD FOR THE PERIOD FROM " + start + " TO " + end + ": " + profit + " KGS");
        Manager.mMenu();
    }
    static void getBoughtTickets() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        ResultSet resultSet = MyConnection.statement.executeQuery(
                "SELECT * FROM visitors WHERE password = '" + Visitor.getPassword() + "';");
        String visitorData = null;
        if (resultSet.next()) visitorData = resultSet.getString(1);
        String visitorID = visitorData;
        ResultSet resultSet2 = MyConnection.statement.executeQuery(
                "SELECT t.ticketID, m.movieName, h.hallName, s.startTime,  t.rowW, t.place, t.ticketPrice " +
                        "FROM sessions s, movies m, halls h, visitors v, tickets t " +
                        "WHERE s.hallID = h.hallID AND s.movieID = m.movieID " +
                        "AND s.sessionID = t.sessionID AND " + visitorID + " = t.visitorID " +
                        "AND firstName = '" + Visitor.getFirstName() +
                        "' ORDER BY startTime;");
        System.out.printf("\n%-20s%-30s%-10s%-30s%-15s%-15s%-20s%n", "TICKET ID", "MOVIE", "HALL", "START TIME", "ROW", "PLACE", "TICKET'S PRICE");
        System.out.println();
        while (resultSet2.next()) {
            System.out.printf("%-20s", resultSet2.getString(1));
            System.out.printf("%-30s", resultSet2.getString(2));
            System.out.printf("%-10s", resultSet2.getString(3));
            System.out.printf("%-30s", resultSet2.getString(4));
            System.out.printf("%-15s", resultSet2.getString(5));
            System.out.printf("%-15s", resultSet2.getString(6));
            System.out.printf("%-20s", resultSet2.getString(7));
            System.out.println();
        } Visitor.vMenu();
    }
    static void findTicket() throws ClassNotFoundException, SQLException, IOException {

        Scanner scanner = new Scanner(System.in);
        Class.forName("com.mysql.cj.jdbc.Driver");

        System.out.println();
        System.out.print("\nENTER THE MOVIE: ");
        String movie = scanner.nextLine();
        ResultSet resultSet = MyConnection.statement.executeQuery(
                "SELECT t.ticketID, s.startTime, m.movieName, h.hallName, t.rowW, t.place, t.ticketPrice" +
                " FROM movies m, halls h, sessions s, tickets t  " +
                "WHERE movieName = '" + movie + "' AND status = 'available' AND m.movieID = s.movieID AND h.hallID = s.hallID AND s.sessionID = t.sessionID;");

        System.out.printf("\n%-15s%-30s%-30s%-15s%-10s%-10s%-10s%n", "TICKET ID", "START TIME", "MOVIE", "HALL", "ROW", "PLACE", "PRICE");
        System.out.println();
        while (resultSet.next()) {
            System.out.printf("%-15s", resultSet.getString(1));
            System.out.printf("%-30s", resultSet.getString(2));
            System.out.printf("%-30s", resultSet.getString(3));
            System.out.printf("%-15s", resultSet.getString(4));
            System.out.printf("%-10s", resultSet.getString(5));
            System.out.printf("%-10s", resultSet.getString(6));
            System.out.printf("%-10s", resultSet.getString(7));
            System.out.println();
        }Visitor.vMenu();
    }
    static void buyTicket() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nENTER THE TICKET ID: ");
        int ticketID = scanner.nextInt();
        ResultSet ticketResultSet = MyConnection.statement.executeQuery("SELECT * FROM tickets WHERE ticketID = " + ticketID + ";");
        int ticketPrice = 0;
        String sessionData = null;
        String ticketStatusCheck = null;
        if (ticketResultSet.next()) {
            sessionData = ticketResultSet.getString(2);
            ticketPrice = ticketResultSet.getInt(4);
            ticketStatusCheck = ticketResultSet.getString(8);
        }
        String sessionID = sessionData;
        String ticketStatus = ticketStatusCheck;

        if (ticketStatus.equals("available")) {
            ResultSet resultSet = MyConnection.statement.executeQuery(
                    "SELECT * FROM visitors WHERE password = '" + Visitor.getPassword() + "';");
            String visitorData = null;
            int balance = 0;
            if (resultSet.next()) {
                visitorData = resultSet.getString(1);
                balance = resultSet.getInt(5);
            }

            String visitorID = visitorData;
            if (balance > ticketPrice) {
                MyConnection.statement.executeUpdate("UPDATE tickets SET status = 'sold' " +
                        "WHERE ticketID = " + ticketID + ";");
                MyConnection.statement.executeUpdate("UPDATE tickets SET visitorID = " + visitorID +
                    " WHERE ticketID = " + ticketID + ";");
                MyConnection.statement.executeUpdate("UPDATE tickets SET dateOfPurchase = now() " +
                    "WHERE ticketID = " + ticketID + ";");
                MyConnection.statement.executeUpdate("UPDATE sessions SET numberOfAvailableTickets = (numberOfAvailableTickets - 1) " +
                    "WHERE sessionID = '" + sessionID + "';");
                MyConnection.statement.executeUpdate("UPDATE sessions SET numberOfSoldTickets = (numberOfSoldTickets + 1)  " +
                    "WHERE sessionID = '" + sessionID + "';");
                MyConnection.statement.executeUpdate("UPDATE visitors SET balance = (balance - " + ticketPrice + ")  " +
                    "WHERE password = '" + Visitor.getPassword() + "';");
                System.out.println("\nDATA SAVED");
                Visitor.vMenu();
            } else {
                System.out.println("\nYOU DON'T HAVE ENOUGH MONEY TO BUY THIS TICKET");
                Visitor.vMenu();
            }
        } else {
            System.out.println("\nTHIS TICKET HAS ALREADY BEEN SOLD");
            Visitor.vMenu();
        }
    }
}


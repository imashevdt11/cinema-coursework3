package Interfaces;

import Other.MyConnection;
import Accounts.Admin;

import java.io.IOException;
import java.util.*;
import java.sql.*;

public interface Session {
    static void addSession() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner intScanner = new Scanner(System.in);
        Scanner scanner = new Scanner(System.in);
        int availableTickets = 0;
        int expensiveTickets = 0;
        int standardTickets = 0;
        int economyTickets = 0;
        int movieDuration = 0;
        int numberOfRows = 0;
        int numberOfPlaces = 0;
        String endTime;
        System.out.println("""
                Before you start entering data, I would like to remind you of the following points:
                                
                - there are only 24 hours in a day
                - there are only 12 months in a year
                - there are only 60 minutes in an hour
                - you can add sessions only for this, 2022, year
                - there are only 28 days in February 2022, 30 days in even months and 31 days in odd months");
                
                Please, keep this in mind and enter the correct data to save your time.
                """);
        try {
            System.out.println("\nENTER THE SESSION'S START TIME. ENTER THE DATE IN NUMERICAL FORMAT.");
            System.out.print("HOUR: ");
            int hour = intScanner.nextInt();
            System.out.print("MINUTE: ");
            int minute = intScanner.nextInt();
            System.out.print("MONTH: ");
            int month = intScanner.nextInt();
            System.out.print("DAY: ");
            int day = intScanner.nextInt();
            if ((hour < 0 || hour > 23) || (minute < 0 || minute > 59) || (month <= 0 || month > 12) ||
                    (month == 2 && day > 28) || (month % 2 == 0 && day > 30) || (month % 2 != 0 && day > 31))
                Admin.aMenu();
            String startTime = "2022-" + month + "-" + day + " " + hour + ":" + minute + ":00";
            ResultSet resultSetAdmin = MyConnection.statement.executeQuery("SELECT * FROM admins WHERE password = '" + Admin.getPassword() + "';");
            String adminID = null;
            if (resultSetAdmin.next()) adminID = resultSetAdmin.getString(1);
            System.out.print("MOVIE'S NAME: ");
            String movieName = scanner.nextLine();
            ResultSet resultSetMovie = MyConnection.statement.executeQuery("SELECT * FROM movies WHERE moviename = '" + movieName + "';");
            String movieID = null;
            if (resultSetMovie.next()) {
                movieID = resultSetMovie.getString(1);
                movieDuration = resultSetMovie.getInt(3);
            } else {
                System.out.println("\nMOVIE IS NOT IN THE DATABASE");
                Admin.aMenu();
            }
            System.out.print("HALL'S NAME: ");
            String hallName = scanner.nextLine();
            ResultSet resultSetHall = MyConnection.statement.executeQuery("SELECT * FROM halls WHERE hallName = '" + hallName + "';");
            String hallID = null;
            if (resultSetHall.next()) {
                hallID = resultSetHall.getString(1);
                availableTickets = resultSetHall.getInt(4) * resultSetHall.getInt(5);
                economyTickets = availableTickets / 4;
                expensiveTickets = availableTickets / 4;
                standardTickets = availableTickets / 2;
                numberOfRows = resultSetHall.getInt(4);
                numberOfPlaces = resultSetHall.getInt(5);
            } else {
                System.out.println("\nHALL IS NOT IN THE DATABASE");
                Admin.aMenu();
            }
            int convertToHours = movieDuration / 60;
            int leftMinutes = movieDuration % 60;
            if (leftMinutes + minute > 60) {
                convertToHours += ((leftMinutes + minute) / 60);
                minute = (leftMinutes + minute) - 60;
            } else minute += leftMinutes;
            endTime = "2022-" + month + "-" + day + " " + (hour + convertToHours) + ":" + minute + ":00";
            boolean isSessionExists = false;
            PreparedStatement prepareStatement = MyConnection.connection.prepareStatement("SELECT * FROM sessions " +
                    "WHERE (startTime BETWEEN '" + startTime + "' AND '" + endTime + "' " +
                    "OR endTime BETWEEN '" + startTime + "' AND '" + endTime + "') AND hallID = '" + hallID + "';");{
                try (ResultSet resultSet = prepareStatement.executeQuery()) {
                    if (resultSet.next()) {isSessionExists = true;}
                }
            }
            if (isSessionExists) {
                System.out.println("\nADDING A SESSION IS NOT POSSIBLE. ANOTHER SESSION HAS ALREADY BEENA SCHEDULED FOR A GIVEN TIME IN THIS HALL");
                Admin.aMenu();
            }
            MyConnection.statement.executeUpdate("INSERT INTO sessions " +
                    "VALUES(null, " + hallID + ", " + movieID + ", " + adminID + ", '" + startTime + "', '" + endTime + "', " +
                    economyTickets + ", " + standardTickets + ", " + expensiveTickets + ", " + availableTickets + ", 0)");
            ResultSet resultSetSession = MyConnection.statement.executeQuery("SELECT * FROM sessions WHERE startTime = '" + startTime + "';");
            String sessionID = null;
            if (resultSetSession.next()) sessionID = resultSetSession.getString(1);
            System.out.print("\nECONOMY TICKET PRICE: ");
            String economyTicketPrice = scanner.nextLine();
            System.out.print("STANDARD TICKET PRICE: ");
            String standardTicketPrice = scanner.nextLine();
            System.out.print("EXPENSIVE TICKET PRICE: ");
            String expensiveTicketPrice = scanner.nextLine();
            for (int j = 1; j <= numberOfRows; j++) {
                if (j <= numberOfRows / 4) {
                    for (int i = 1; i <= numberOfPlaces; i++) {
                        MyConnection.statement.executeUpdate("INSERT INTO tickets(sessionID, ticketPrice, type, rowW, place) " +
                                "VALUES(" + sessionID + ", " + economyTicketPrice + ", 'economy', " + j + ", " + i + ");");
                    }
                } else if (j > (numberOfRows / 4) && j <= (numberOfRows / 2) + (numberOfRows / 4)) {
                    for (int i = 1; i <= numberOfPlaces; i++) {
                        MyConnection.statement.executeUpdate("INSERT INTO tickets(sessionID, ticketPrice, type, rowW, place) " +
                                "VALUES(" + sessionID + ", " + standardTicketPrice + ", 'standard', " + j + ", " + i + ");");
                    }
                } else if (j > (numberOfRows / 2) + (numberOfRows / 4)) {
                    for (int i = 1; i <= numberOfPlaces; i++) {
                        MyConnection.statement.executeUpdate("INSERT INTO tickets(sessionID, ticketPrice, type, rowW, place) " +
                                "VALUES(" + sessionID + ", " + expensiveTicketPrice + ", 'expensive', " + j + ", " + i + ");");
                    }
                }
            }
            System.out.println("\nTHE SESSION IS ADDED");
            Admin.aMenu();
        } catch (InputMismatchException inputMismatchException) {
            System.out.println("\nPLEASE, ENTER DATE AND TIME DATA IN NUMERIC FORMAT");
            addSession();
        }
    }
    static void getSchedule() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);

        System.out.print("ENTER THE DATE (FOR EXAMPLE, 2018-11-23): ");
        String date = scanner.nextLine();
        String queryTime = "'" + date + " 00:00:00' AND '" + date + " 23:59:59'";

        ResultSet resultSet = MyConnection.statement.executeQuery(
                "SELECT s.startTime, s.endTime, m.movieName, h.hallName, s.numberOfAvailableTickets, s.numberOfSoldTickets " +
                        "FROM sessions s, movies m, halls h " +
                        "WHERE startTime BETWEEN " + queryTime + " AND s.hallID = h.hallID AND s.movieID = m.movieID ORDER BY startTime;");

        System.out.printf("\n%-25s%-25s%-30s%-10s%-30s%-30s%n", "START TIME", "END TIME", "MOVIE", "HALL", "NUMBER OF AVAILABLE TICKETS",
                "NUMBER OF SOLD TICKETS");
        System.out.println();
        while (resultSet.next()) {
            System.out.printf("%-25s", resultSet.getString(1));
            System.out.printf("%-25s", resultSet.getString(2));
            System.out.printf("%-30s", resultSet.getString(3));
            System.out.printf("%-10s", resultSet.getString(4));
            System.out.printf("%-30s", resultSet.getString(5));
            System.out.printf("%-30s", resultSet.getString(6));
            System.out.println();
        }
    }
}


package Interfaces;

import Other.MyConnection;
import Accounts.Admin;

import java.io.IOException;
import java.util.Scanner;
import java.sql.*;

public interface Movie {
    static void addMovie() throws ClassNotFoundException, NumberFormatException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nMOVIE'S NAME: ");
        String movieName = scanner.nextLine();
        boolean isMovieExists = false;
        PreparedStatement prepareStatement = MyConnection.connection.prepareStatement("SELECT moviename FROM movies WHERE moviename = ?");{
            prepareStatement.setString(1, movieName);
            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) isMovieExists = true;}}
        if (isMovieExists) {
            System.out.println("\nTHIS MOVIE IS ALREADY IN DATABASE");
            Admin.aMenu();
        }
        else {
            try {
                System.out.print("DURATION(MINUTES) (MUST BE GREATER THAN 60 AND LESS THAN 240): ");
                String duration = scanner.nextLine();
                System.out.print("COUNTRY (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
                String country = scanner.nextLine();
                System.out.print("GENRE (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
                String genre = scanner.nextLine();
                System.out.print("PRODUCTION YEAR (MUST BE FOUR DIGITS): ");
                String productionYear = scanner.nextLine();
                System.out.print("PRODUCER (MUST CONTAIN LESS THAN 20 CHARACTERS): ");
                String producer = scanner.nextLine();
                if (Integer.parseInt(productionYear) > 2022 || Integer.parseInt(productionYear) < 1900) {
                    System.out.print("""
                            \nYOU ENTERED PRODUCTION YEAR INCORRECTLY
                                                        
                            DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
                    String choice = scanner.nextLine();
                    if (choice.equals("1")) addMovie();
                    else Admin.aMenu();
                } else if (country.length() > 20 || genre.length() > 20 || producer.length() > 20) {
                    System.out.print("""
                            \nYOU HAVE ENTERED THE NUMBER OF CHARACTERS EXCEEDING THE SPECIFIED LIMIT
                                                        
                            DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
                    String choice = scanner.nextLine();
                    if (choice.equals("1")) addMovie();
                    else Admin.aMenu();
                } else if (Integer.parseInt(duration) > 240 || Integer.parseInt(duration) < 60) {
                    System.out.print(""" 
                            \nYOU HAVE ENTERED AN INVALID NUMBER OF MINUTES
                                                        
                            DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
                    String choice = scanner.nextLine();
                    if (choice.equals("1")) addMovie();
                    else Admin.aMenu();
                } else {
                    MyConnection.statement.executeUpdate("INSERT into movies " +
                            "VALUES(NULL, '" + movieName + "', " + duration + ", '" +
                            country + "', '" + genre + "', " + productionYear + ", '" + producer + "');");
                    System.out.println("\nTHE MOVIE HAS BEEN ADDED TO THE DATABASE");
                    Admin.aMenu();
                }
            } catch (NumberFormatException numberFormatException) {
                System.out.println("\nPLEASE, ENTER THE MOVIE'S DURATION AND PRODUCTION YEAR IN NUMERIC FORMAT");
                addMovie();
            }
        }
    }
    static void getMovies() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        ResultSet resultSet = MyConnection.statement.executeQuery("SELECT * FROM movies;");
        System.out.printf("\n%-35s%-30s%-20s%-20s%-30s%-20s%n", "MOVIE", "DURATION(MINUTES)", "COUNTRY", "GENRE", "PRODUCER", "PRODUCTION YEAR");
        System.out.println();
        while (resultSet.next()) {
            System.out.printf("%-35s", resultSet.getString(2));
            System.out.printf("%-30s", resultSet.getString(3));
            System.out.printf("%-20s", resultSet.getString(4));
            System.out.printf("%-20s", resultSet.getString(5));
            System.out.printf("%-30s", resultSet.getString(7));
            System.out.printf("%-20s", resultSet.getString(6));
            System.out.println();
        }
    }
}



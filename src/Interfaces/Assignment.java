package Interfaces;

import Other.MyConnection;
import Accounts.*;

import java.io.IOException;
import java.util.Scanner;
import java.sql.*;

public interface Assignment {
    static void completeAssignment() throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nENTER THE ASSIGNMENT ID: ");
        String assignmentID = scanner.nextLine();
        boolean isAssignmentExists = false;
        PreparedStatement preparedStatement = MyConnection.connection.prepareStatement("SELECT assignmentID " +
                "FROM assignments WHERE assignmentID = ?;");
        {
            preparedStatement.setString(1, assignmentID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) isAssignmentExists = true;
            }
        }
        if (isAssignmentExists) {
            boolean isAssignmentComplete = false;
            PreparedStatement preparedStatement2 = MyConnection.connection.prepareStatement("SELECT * FROM assignments " +
                    "WHERE assignmentID = ? AND status = 'not done';");
            {
                preparedStatement2.setString(1, assignmentID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) isAssignmentComplete = true;
                }
            }
            ResultSet resultSet = MyConnection.statement.executeQuery("SELECT * FROM admins " +
                    "WHERE firstName = '" + Admin.getFirstName() + "' AND lastName = '" +  Admin.getLastName() + "';");
            String adminID = null;
            while (resultSet.next()) {
                adminID = resultSet.getString(1);
            }
            if (isAssignmentComplete){
                MyConnection.statement.executeUpdate("UPDATE assignments SET status = 'done' WHERE assignmentID = " + assignmentID + ";");
                MyConnection.statement.executeUpdate("UPDATE assignments SET dateOfCompletion = now() WHERE assignmentID = " + assignmentID + ";");
                MyConnection.statement.executeUpdate("UPDATE admins SET numberOfCompletedAssignments = (numberOfCompletedAssignments + 1) " +
                        "WHERE password = '" + Admin.getPassword() + "';");
                MyConnection.statement.executeUpdate("UPDATE assignments SET adminID = " + adminID + " WHERE assignmentID = " + assignmentID + ";");
                System.out.println("\nDATA SAVED");
                Admin.aMenu();
            }
            else  {
                System.out.println("THIS ASSIGNMENT HAS ALREADY BEEN COMPLETED");
                Admin.aMenu();
            }
        }
        else {
            System.out.println("THE ASSIGNMENT WITH THE SPECIFIED ID IS NOT IN THE DATABASE");
            Admin.aMenu();
        }
    }
    static void requestAssignment() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nENTER THE ASSIGNMENT (MUST CONTAIN LESS THAN 80 CHARACTERS): ");
        String assignment = scanner.nextLine();
        if (assignment.length() > 80) {
            System.out.print("""
                    \nYOU HAVE ENTERED THE NUMBER OF CHARACTERS EXCEEDING THE SPECIFIED LIMIT
                    
                    DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
            String choice = scanner.nextLine();

            if (choice.equals("1")) requestAssignment();
            else Manager.mMenu();
        }
        ResultSet resultSet = MyConnection.statement.executeQuery("SELECT * FROM managers WHERE password = '" + Manager.getPassword() + "';");

        String managerData = null;
        if (resultSet.next()) managerData = resultSet.getString(1);
        String managerID = managerData;

        MyConnection.statement.executeUpdate("INSERT INTO assignments(managerID, assignment) VALUES(" + managerID + ", '" + assignment + "');");
        System.out.println("\nTHE ASSIGNMENT IS REQUESTED");
        Manager.mMenu();
    }
    static void getAssignments() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Scanner scanner = new Scanner(System.in);
        System.out.print("DO YOU WANT TO GET LIST OF COMPLETED ASSIGNMENTS OR NOT COMPLETED? (1 - COMPLETED / 2 - NOT COMPLETED): ");
        String choice = scanner.nextLine();
        if (choice.equals("1")) {
            ResultSet resultSet = MyConnection.statement.executeQuery("SELECT s.assignment, s.dateOfCompletion, a.firstName, a.lastName " +
                    "FROM assignments s, admins a WHERE a.adminID = s.adminID;");
            System.out.printf("\n%-80s%-25s%-40s%n", "ASSIGNMENT", "DATE OF COMPLETION", "ADMIN");
            System.out.println();
            while (resultSet.next()) {
                System.out.printf("%-80s", resultSet.getString(1));
                System.out.printf("%-25s", resultSet.getString(2));
                System.out.printf(resultSet.getString(3) + " " + resultSet.getString(4));
                System.out.println();}}
        else if (choice.equals("2")) {
            ResultSet resultSet = MyConnection.statement.executeQuery("SELECT assignmentID, assignment, dateOfAppointment " +
                    "FROM assignments s WHERE status = 'not done';");
            System.out.printf("\n%-20s%-90s%-30s%n", "ASSIGNMENT ID", "ASSIGNMENT", "DATE OF APPOINTMENT");
            System.out.println();
            while (resultSet.next()) {
                System.out.printf("%-20s", resultSet.getString(1));
                System.out.printf("%-90s", resultSet.getString(2));
                System.out.printf("%-30s", resultSet.getString(3));
                System.out.println();}}
    }
}
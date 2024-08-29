package Accounts;
import Interfaces.*;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Scanner;

public abstract class User implements Assignment, Movie, Review, Session, Ticket {
    private static String firstName;
    private static String lastName;
    private static String password;
    public static void typeChoosing() throws IOException, ClassNotFoundException, SQLException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————");
        System.out.print("""
                \nENTER THE NUMBER OF YOUR ACCOUNT TYPE
        
                1 - ADMINISTRATOR
                2 - VISITOR
                3 - MANAGER
        
                4 - SIGN UP
        
                0 - SHUT DOWN THE PROGRAMME:\040""");
        String choice = scanner.nextLine();
        System.out.println("\n———————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————————");

        switch (choice) {

            case "1" -> Admin.aLogIn();
            case "2" -> Visitor.vLogIn();
            case "3" -> Manager.mLogIn();

            case "4" -> Visitor.signUp();

            case "0" -> System.out.println("\nGOODBYE! HAVE A NICE DAY!\n");
            default -> {
                System.out.print("""
                        \nINVALID ACCOUNT TYPE NUMBER
                        
                        DO YOU WANT TO TRY AGAIN? (1 - YES / 0 - NO):\040""");
                choice = scanner.nextLine();

                if (choice.equals("1")) typeChoosing();
                else System.out.println("\nGOODBYE! HAVE A NICE DAY!\n");
            }
        }
    }
    static void setFirstName(String firstName) {
        User.firstName = firstName;
    }
    static void setLastName(String lastName) {
        User.lastName = lastName;
    }
    static void setPassword(String password) {
        User.password = password;
    }
    public static String getFirstName() {
        return firstName;
    }
    public static String getLastName() {
        return lastName;
    }
    public static String getPassword() {
        return password;
    }
}



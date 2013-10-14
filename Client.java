import java.util.Date;
import java.util.Scanner;

public class Client {


    public static String AskUsername(Scanner sc){
        System.out.print("Enter username: ");
        return sc.nextLine();
    }

    public static String AskPassword(Scanner sc){
        System.out.print("Enter password: ");
        return sc.nextLine();
    }

    static public void main(String[] args) {
        ClientConnection conn = new ClientConnection();
        Scanner sc = new Scanner(System.in);
        String username, pass;

        //  Connects to the TCP Primary Server
        conn.connect();

        //Makes login
        boolean login_result = conn.login("Hakuna","Matat");

        while (!login_result){
            System.out.println("\nLogin failed\n1- Enter username and password again\n2- Register\nYour choice:");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 2){
                System.out.println("Enter your username:");
                username = sc.nextLine();

                System.out.println("Enter your password:");
                pass = sc.nextLine();

                System.out.println("Enter your email address:");
                String email = sc.nextLine();

                Date date = new Date();//Get current date

                if (!conn.register(username,pass,email,date))
                    System.out.println("ERROR IN THE REGISTER!!!!!");

                //Now tht the registration is sucessfull is time to login
                System.out.println("Registration sucessfull :)");
            }

            username = AskUsername(sc);
            pass = AskPassword(sc);

            System.out.println(username + " " + pass);
            login_result = conn.login(username,pass);
        }

        ClientTopic[] topics = conn.getTopics();

        for (Topic t : topics)
            System.out.println(t);

        for (;;);
    }
}

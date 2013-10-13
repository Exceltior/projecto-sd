import java.util.Date;
import java.util.Scanner;

public class Client {
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

            if (choice == 1){
                System.out.print("Enter username: ");
                username = sc.nextLine();

                sc.nextLine();//Clear the '\n' in the buffer

                System.out.print("Enter password: ");
                pass = sc.nextLine();
                login_result = conn.login(username,pass);

            }else{
                System.out.println("Enter your username:");
                username = sc.nextLine();

                //Clear the '\n' in the buffer
                sc.nextLine();

                System.out.println("Enter your password:");
                pass = sc.nextLine();

                //Clear the '\n' in the buffer
                sc.nextLine();

                System.out.println("Enter your email address:");
                String email = sc.nextLine();

                //Clear the '\n' in the buffer
                sc.nextLine();

                Date date = new Date();//Get current date

                if (!conn.register(username,pass,email,date))
                    System.out.println("ERROR IN THE REGISTER!!!!!");
            }
        }

        ClientTopic[] topics = conn.getTopics();

        for (Topic t : topics)
            System.out.println(t);

        for (;;);
    }
}

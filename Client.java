import java.util.Date;
import java.util.Scanner;

public class Client {

    private ClientConnection conn;
    private String username;
    private String password;

    Client(String user, String pass){
        super();
        conn = new ClientConnection();
        username = user;
        password = pass;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public String AskUsername(Scanner sc){
        System.out.print("Enter username: ");
        return sc.nextLine();
    }

    public String AskPassword(Scanner sc){
        System.out.print("Enter password: ");
        return sc.nextLine();
    }

    public ClientConnection getConnection(){
        return this.conn;
    }

    ////
    //  Method responsible for collecting the information needed to create a new topic, and send a request to the TCP Server
    //  in order to create that new topic in the database
    ////
    public boolean createTopic(Scanner sc,ClientConnection conn){
        String nome, descricao;

        System.out.println("Insira o nome do topico:");
        nome = sc.nextLine();

        System.out.println("Insira a descricao do topico:");
        descricao = sc.nextLine();

        return conn.createTopic(nome,descricao);
    }

    static public void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Client cliente = new Client("Hakuna", "Matata");
        String username = cliente.getUsername(), pass = cliente.getPassword();
        ClientConnection conn = cliente.getConnection();

        //  Connects to the TCP Primary Server
        conn.connect();

        //Makes login
        boolean login_result = conn.login(username,pass), stay = true;

        while (!login_result){
            System.out.println("\nLogin failed\n1- Enter username and password again\n2- Register\nYour choice:");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 2){
                while (stay){

                    stay = false;

                    System.out.println("Enter your username:");
                    username = sc.nextLine();

                    System.out.println("Enter your password:");
                    pass = sc.nextLine();

                    System.out.println("Enter your email address:");
                    String email = sc.nextLine();

                    Date date = new Date();//Get current date

                    if (!conn.register(username,pass,email,date)){
                        do{
                            System.out.print("Registration unsucessfull :(\n1-Try registration again\n2-try login in with another username?\nYour choice: ");
                            choice = sc.nextInt();
                            sc.nextLine();//Clear the buffer

                            ////
                            //  If the user insert inserts "2" (try login with another username) we don't need to do anything,
                            //  because it will be the next thing he sees
                            ////

                            if (choice == 1)
                                stay = true;

                        } while (choice!=1 && choice!=2);
                    }
                    else //Now tht the registration is sucessfull is time to login
                        System.out.println("Registration sucessfull :)");

                }
            }

            username = cliente.AskUsername(sc);
            pass = cliente.AskPassword(sc);

            login_result = conn.login(username,pass);
            System.out.println("O login deu " + login_result);
        }

        if (!cliente.createTopic(sc,conn)){
            System.out.println("Erro ao criar um topico! Topico já existe");
            return ;
        }

        ClientTopic[] topics = conn.getTopics();

        for (Topic t : topics)
            System.out.println(t);

        for (;;);
    }
}

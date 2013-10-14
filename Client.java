import java.util.Date;
import java.util.Scanner;

public class Client {

    private int id;
    private ClientConnection conn;
    private String username;
    private String password;

    Client(){
        super();
        conn = new ClientConnection();
    }

    public int getId(){
        return this.id;
    }

    public void setId(int i){
        this.id = i;
    }

    public void setUsername(String u){
        this.username = u;
    }

    public void setPassword(String p){
        this.password = p;
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

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public boolean createTopic(Scanner sc,ClientConnection conn){
        String nome, descricao;

        System.out.println("Insira o nome do topico:");
        nome = sc.nextLine();

        System.out.println("Insira a descricao do topico:");
        descricao = sc.nextLine();

        return conn.createTopic(nome,descricao, this.id);
    }

    static public void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String username = "Hakuna", pass = "Matata";
        Client cliente = new Client();
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


        cliente.setUsername(username);
        cliente.setPassword(pass);
        cliente.setId(conn.getClientID(cliente.getUsername()));

        ClientTopic[] topics = conn.getTopics();

        for (Topic t : topics)
            System.out.println(t);

        System.out.println(cliente.createTopic(sc,conn));

        for (;;);
    }
}

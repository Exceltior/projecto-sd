import java.util.Date;
import java.util.Scanner;

public class Client {

    private ClientConnection conn;
    private String username;
    private String password;

    Client(){
        super();
        conn = new ClientConnection();
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public void setUsername(String user){
        this.username = user;
    }

    public void setPassword(String password) {
        this.password = password;
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
    private boolean createTopic(Scanner sc,ClientConnection conn){
        String nome, descricao;

        System.out.println("Please enter the name of the topic:");
        nome = sc.nextLine();

        System.out.println("Please enter the description of the topic:");
        descricao = sc.nextLine();

        return conn.createTopic(nome,descricao);
    }

    ////
    //  Method responsible for collecting the information needed to create a new idea, and send a request to the TCP Server in
    //  order to create that new topic in the database
    ////
    private boolean createIdea(Scanner sc, ClientConnection conn){
        String  title, description;

        System.out.println("Please enter the title of the idea:");
        title = sc.nextLine();

        System.out.println("Please enter the description of the idea:");
        description = sc.nextLine();

        //FIXME: Recolher ids a favor e contra

        return conn.createIdea(title, description);
    }

    public static int Menu(Scanner sc){
        int choice;

        System.out.println("\n\nMain Menu");
        System.out.println("1 - Check a topic");//List all topics and choose one. While "inside" a topic list all ideas
        System.out.println("2 - Create a new topic");
        System.out.println("3 - Submit an idea");
        System.out.print("Your choice: ");

        ////
        //  FIXME: INCOMPLETE, THERE ARE OPTIONS MISSING!!!!!!!
        ////

        choice = sc.nextInt();
        sc.nextLine();

        return choice;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Client cliente = new Client();
        ClientConnection conn = cliente.getConnection();
        String username = "", password = "", email = "";
        int choice;
        boolean login_result = false, stay = true;

        //  Connects to the TCP Primary Server
        conn.connect();

        do{
            System.out.println("\n               Welcome!");
            System.out.println("--------------------------------------------------");
            System.out.println("There is no current session opened. Please select how you wish to connect:");
            System.out.println("1 - Login");
            System.out.println("2 - Register");
            System.out.print("Your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            if (choice < 1 || choice > 2){
                System.out.println("Invalid Choice!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.err.println("Client thread was interrupted");
                    //FIXME: WHAT TO DO WITH THIS EXCEPTION????
                }
            }

        }while (choice < 1 || choice > 2);


        while (!login_result){

            System.out.println("Please enter your username:");
            username = sc.nextLine();

            System.out.println("Please enter your password:");
            password = sc.nextLine();

            if (choice == 2){
                while (stay){
                    stay = false;

                    System.out.println("Enter your email address:");
                    email = sc.nextLine();

                    Date date = new Date();//Get current date

                    if (!conn.register(username,password,email,date)){
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
                        System.out.println("Registration sucessfull");
                }
            }

            login_result = conn.login(username,password);

            if (!login_result)
                System.out.println("Login unsucessfull!\n");
        }
        ////
        //  Login was successfull
        ////

        while(true){
            choice = Menu(sc);

            switch(choice){

                //Check a topic - List all the topcis and ask the user which one he wants. While "inside" a topic list all ideas
                case 1:
                {
                    ClientTopic[] topics = conn.getTopics();

                    System.out.println("\n");
                    for (Topic t : topics)
                        System.out.println(t);

                    System.out.print("Which topic do you want to see? ");
                    int selected = sc.nextInt();

                    ////
                    //  FIXME: NEED TO IMPLEMENT THIS PART: TOPIC HAS BEEN SELECTED, IT'S TIME TO SHOW ITS IDEAS
                    ////

                    break;
                }

                //Create a new topic
                case 2:
                    if (!cliente.createTopic(sc,conn))
                        System.out.println("Error while creating a topic! Topic already exists");
                    break;

                //Submit an idea
                case 3:
                    if (!cliente.createIdea(sc,conn))
                        System.out.println("Error while creating an idea! Idea already exists");
                    break;

                //Wrong choice
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }
}

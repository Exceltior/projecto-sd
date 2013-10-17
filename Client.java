import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    private ClientConnection conn;
    private String username;
    private String password;
    private String email;
    private Scanner sc;

    Client(){
        super();
        conn = new ClientConnection();
        sc = new Scanner(System.in);
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

    private String askUsername(){
        System.out.print("Enter username: ");
        return sc.nextLine();
    }

    private String askPassword(){
        System.out.print("Enter password: ");
        return sc.nextLine();
    }

    private String askEmail(){
        System.out.print("Enter your email address: ");
        return sc.nextLine();
    }

    public ClientConnection getConnection(){
        return this.conn;
    }

    ////
    //  Method responsible for collecting the information needed to create a new topic, and send a request to the TCP Server
    //  in order to create that new topic in the database
    ////
    private boolean createTopic(){
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
    private boolean createIdea(){
        String  title, description, topics;
        int nshares, price;

        System.out.println("Please enter the title of the idea:");
        title = sc.nextLine();

        System.out.println("Please enter the description of the idea:");
        description = sc.nextLine();

        System.out.println("Please enter the number of shares for the idea:");
        nshares = sc.nextInt();

        System.out.println("Please enter the price of each share of the idea:");
        price = sc.nextInt();

        sc.nextLine();//Clear the buffer

        System.out.println("Please enter the titles of the topics where you want to include your idea (USAGE: topic1;topic2)");
        topics = sc.nextLine();

        //FIXME: Recolher ids a favor e contra + topicos

        return conn.createIdea(title, description,nshares,price,topics) > 0;
    }

    ////
    //  Prints the Welcome Screen, when the users connects to the Server
    ////
    private int printWelcomeScreen(){
        int choice;

        System.out.println("\n               Welcome!");
        System.out.println("--------------------------------------------------");
        System.out.println("There is no current session opened. Please select how you wish to connect:");
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.print("Your choice: ");
        try{
            choice = sc.nextInt();
            sc.nextLine();
        }catch(InputMismatchException m){
            choice = 5;//So that we run the cicle again
        }

        return choice;
    }

    ////
    //  Prints the main menu screen, where the user can select what actions to do
    ////
    private int Menu(Scanner sc){
        int choice = -1;
        boolean repeat = false;

        System.out.println("\n\nMain Menu");
        System.out.println("1 - Check a topic");//List all topics and choose one. While "inside" a topic list all ideas
        System.out.println("2 - Create a new topic");
        System.out.println("3 - Submit an idea");
        System.out.println("4 - Delete an idea");
        System.out.println("0 - Sair");

        do{
            System.out.print("Your choice: ");

        ////
        //  FIXME: INCOMPLETE, THERE ARE OPTIONS MISSING!!!!!!!
        ////
            try{
                choice = sc.nextInt();
                sc.nextLine();
            }catch(InputMismatchException m){
                repeat = true;
            }

        }while(repeat);

        return choice;
    }

    private void execute(){
        int choice;
        boolean login_result = false, stay = true;

        //  Connects to the TCP Primary Server
        conn.connect();

        do{
            choice = printWelcomeScreen();

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

            username = askUsername();
            password = askPassword();

            if (choice == 2){
                while (stay){
                    stay = false;

                    email = askEmail();

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
        System.out.println("Login Successfull!");

        mainLoop();
    }

    private void mainLoop(){
        int choice, selected = -1, min_id_topic = 0, max_id_topic = 0;
        boolean stay = true;

        while(stay){
            choice = Menu(sc);

            switch(choice){

                //Logout
                case 0:{
                    System.out.println("Thank you for posting with us, hope you have a nice day! Goodbye!");
                    stay = false;
                    break;
                }

                //Check a topic - List all the topcis and ask the user which one he wants. While "inside" a topic list all ideas
                case 1:{
                    ClientTopic[] topics = conn.getTopics();

                    if (topics.length>0)
                        min_id_topic = topics[0].getId();

                    System.out.println("\n");
                    for (Topic t : topics){
                        System.out.println(t);
                        if(t.getId() > max_id_topic)
                            max_id_topic = t.getId();
                        else if(t.getId() < min_id_topic)
                            min_id_topic = t.getId();
                    }

                    do{
                        System.out.print("Which topic do you want to see? ");
                        try{
                            selected = sc.nextInt();
                        }catch(InputMismatchException m){
                            selected = -1;
                        }
                    }while (selected < min_id_topic || selected > max_id_topic);

                    ////
                    //  FIXME: NEED TO IMPLEMENT THIS PART: TOPIC HAS BEEN SELECTED, IT'S TIME TO SHOW ITS IDEAS
                    ////

                    break;
                }

                //Create a new topic
                case 2:{
                    if (!createTopic())
                        System.out.println("Error while creating a topic! Topic already exists");
                    break;
                }
                //Submit an idea
                case 3:{
                    if (!createIdea())
                        System.out.println("Error while creating an idea! Idea already exists");
                    else
                        System.out.println("Idea created with success");
                    break;
                }

                //Delete an idea
                case 4:{

                    //First we need to check if there is any idea which is fully owned by the user
                    System.out.println("Delete an idea!!!!");
                    break;
                }
                //Wrong choice
                default:{
                    System.out.println("Invalid option!");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.execute();
    }
}

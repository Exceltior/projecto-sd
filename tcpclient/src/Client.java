import model.data.Idea;
import model.data.NetworkingFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Client {

    private final ClientConnection conn;
    private String username;
    private String password;
    private String email;
    private final Scanner sc;

    private Client(String[] args){
        super();
        conn = new ClientConnection(args);
        sc = new Scanner(System.in);
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

    private String verifyTopicString(String temp){
        if (!temp.trim().contains(" "))
            return temp.trim();
        //Here we know the string has at least one space, lets check if it is at the end of the string
        String substring = temp.substring(0,temp.length()-1);
        if (!substring.trim().contains(" "))//The space is in the last position of the string
            return temp.trim();
        //Note: I left the if unsimplified for better understanding of the code
        return null;
    }

    /**
     * Method to filter the topics inserted by the user. With this method we can discard invalid topic names, like:
     * #this is #topic . In this example we only consider the topics "this" and "topic", since it's impossible to use
     * hashtags with spaces.
     * @param list  An array of String objects, containing a list of the topics inserted by the user
     * @return  An ArrayList of String objects, containing the final valid topics.
     */
    private ArrayList<String> getTopicsFromList(String[] list){
        ArrayList<String> devolve = new ArrayList<String>();
        String temp;

        for (String aList : list) {
            if (!aList.trim().equals("")){
                temp = verifyTopicString(aList);
                if (temp != null && !devolve.contains(temp.trim()))
                    devolve.add(aList.trim());
            }
        }

        return devolve;
    }


    ////
    //  Method responsible for asking the user information about the topics of an idea
    //  atLeastOneElement - Indicates if the user actually needs to insert a topic or not
    ////
    private ArrayList<String> askTopics(String sentence){
        boolean repeat;
        String response;
        String[] temp;
        ArrayList<String> devolve = new ArrayList<String>();

        do{
            repeat = false;
            System.out.println(sentence);
            response = sc.nextLine();

            if ( true && response.equals("")){//Empty String, going to ask the user again
                System.out.println("Invalid input!");
                repeat = true;
            }else if(response.equals(""))
                return devolve;

        }while (repeat);

        return getTopicsFromList(response.split("#"));
    }

    ////
    //  Method responsible for collecting the information needed to create a new idea, and send a request to the TCP Server in
    //  order to create that new topic in the database
    ////
    private boolean createIdea(){
        String title, description, file, filePath;
        ArrayList<String> topics;
        ArrayList<Integer> ideasFor, ideasAgainst, ideasNeutral;
        float initialInvestment = 0;
        NetworkingFile ficheiro = null;
        boolean repeat;
        String line;

        do{
            repeat = false;
            System.out.println("Please enter the title of the idea: (If you want to go back please enter -1)");
            title = sc.nextLine();
            if (title.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }else if (title.equals("-1"))
                return false;
        }while (repeat);

        do{
            repeat = false;
            System.out.println("Please enter the description of the idea:");
            description = sc.nextLine();
            if (description.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);


        do{
            repeat = false;
            System.out.println("Please enter the initial Investment:");
            line = sc.nextLine();
            try{
                initialInvestment = Float.parseFloat(line);
                if (initialInvestment <= 0){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while(repeat);

        topics = askTopics("Please enter the titles of the topics where you want to include your idea (USAGE: #TOpic " +
                                   "1 #Topic2" +
                                   ")");

        return conn.createIdea(title, description,topics,initialInvestment);
    }

    ////
    //  Prints the Welcome Screen, when the users connects to the Server
    ////
    private int printWelcomeScreen(){
        int choice;
        String line;

        System.out.println("\n               Welcome!");
        System.out.println("--------------------------------------------------");
        System.out.println("There is no current session opened. Please select how you wish to connect:");
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.print("Your choice: ");
        try{
            line = sc.nextLine();
            choice = Integer.parseInt(line);
        }catch(NumberFormatException n){
            System.out.println("Invalid input!");
            choice = 5;//So that we run the cicle again
        }

        return choice;
    }

    ////
    //  Prints the main menu screen, where the user can select what actions to do
    ////
    private int Menu(Scanner sc){
        int choice = -1;
        String line;
        boolean repeat;

        System.out.println("\n\nMain Menu");
        System.out.println("1 - Create Idea");//List all topics and choose one. While "inside" a topic list all ideas
        System.out.println("2 - Buy Shares of Idea");
        System.out.println("0 - Quit");

        do{
            repeat = false;
            System.out.print("Your choice: ");
            try{
                line = sc.nextLine();
                choice = Integer.parseInt(line);
                if(choice < 0 || choice > 2)
                    repeat = true;
            }catch(NumberFormatException n){
                repeat = true;
            }

        }while(repeat);

        return choice;
    }


    private void execute(){
        int choice;
        int  login_result;
        boolean stay = true;
        String line;

        //  Connects to the TCP Primary Server
        conn.connect();

        /** This is here for TESTING
        conn.login("Joca","teste123");
        conn.deleteIdea(3);

        askUsername(); //Loop on something
        **/

        do{
            choice = printWelcomeScreen();

            if (choice < 1 || choice > 2){
                System.out.println("Invalid Choice!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }

        }while (choice < 1 || choice > 2);

        while (stay){
            username = askUsername();
            password = askPassword();

            if (choice == 2){

                email = askEmail();
                Date date = new Date();//Get current date

                if (!conn.register(username,password,email,date)){
                    do{
                        System.out.print("Registration unsucessful :(\n1-Try login in with another username\n2-Try registration again\nYour choice: ");
                        line = sc.nextLine();
                        try{
                            choice = Integer.parseInt(line);
                            if (choice!=1 && choice!=2)
                                System.out.println("Invalid input!");
                        }catch(NumberFormatException n){
                            System.out.println("Invalid input!");
                            choice = 6;//To enter the loop again
                        }
                        stay = true;
                    } while (choice!=1 && choice!=2);

                    //if (choice==1 || choice==2)
                    continue;
                }
                else{ //Now that the registration is sucessfull is time to login
                    System.out.println("Registration sucessful");
                }
            }

            login_result = conn.login(username,password);

            if (login_result == 3){
                stay = true;
                System.out.println("Login unsucessful!\nIf you want to register just enter 2, otherwise press any key to login again");
                String temp = sc.nextLine();
                try{
                    if (Integer.parseInt(temp) == 2)
                        choice = 2;
                }catch(NumberFormatException ignored){}//We don't need to handle this exception
            }
            else
                stay = false;
        }

        //  Login was successful
        System.out.println("Login Successful!");

        mainLoop();
    }

    private int hasElement(ArrayList<Idea> ideasList, int iid){
        for (int i=0;i<ideasList.size();i++){
            if (ideasList.get(i).getId() == iid)
                return i;
        }
        return -1;
    }

    private boolean buyShares(){
        boolean repeat;
        String line;
        int iid = -1;
        int numberSharesToBuy;
        float sellPrice=0;
        float maxPrice=0;

        //Pedir ao user que ideias quer comprar
        do{
            repeat = false;
            System.out.println("Please insert the id of the idea whose shares you want to buy: (If you want to go back please enter -1");
            line = sc.nextLine();
            try{
                iid = Integer.parseInt(line);
                if (iid == -1)
                    return false;

                if (iid < 1){
                    System.out.println("Invalid option!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid option!");
                repeat = true;
            }
        }while (repeat);

        //Get how many shares are available for the given idea
        do{
            repeat = false;
            System.out.println("Please insert the number of shares you want to buy from the given idea:");
            line = sc.nextLine();
            numberSharesToBuy = Integer.parseInt(line);
            if ( numberSharesToBuy < 0 )
                repeat = true; //I ONLY DO THIS THING THIS WAY BECAUSE JOCA DID IT TOO.
        }while (repeat);

        do{
            repeat = false;
            System.out.println("Please insert the selling price of each share you are going to buy, or -1 if you dont" +
                    " want to change it:");
            line = sc.nextLine();
            try{
                sellPrice = Float.parseFloat(line);
                if(sellPrice != -1 && sellPrice < 0){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        do{

            repeat = false;
            System.out.println("Please insert the maximum price you are willing to give for the shares.");
            line = sc.nextLine();
            try{
                maxPrice = Float.parseFloat(line);
                if(maxPrice <= 0 ){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);


        return conn.buyShares(iid,numberSharesToBuy,maxPrice,sellPrice);
    }

    private void mainLoop(){
        int choice;

        while(true){
            choice = Menu(sc);

            switch(choice){

                //Logout
                case 0:{
                    System.out.println("Thank you for posting with us, hope you have a nice day! Goodbye!");
                    System.exit(0);
                    break;
                }


                //Submit an idea
                case 1:{
                    if (!createIdea())
                        System.out.println("Error while creating an idea!");
                    else
                        System.out.println("Idea created with success");
                    break;
                }

                //Buy shares of an idea
                case 2:{
                    buyShares();
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
        Client client = new Client(args);
        client.execute();
    }
}

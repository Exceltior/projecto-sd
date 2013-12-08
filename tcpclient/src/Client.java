import model.data.Idea;
import model.data.NetworkingFile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
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


    ////
    //  Method responsible for asking the user information about the topics of an idea
    //  atLeastOneElement - Indicates if the user actually needs to insert a topic or not
    ////
    private ArrayList<String> askTopics(String sentence, boolean atLeastOneElement){
        boolean repeat;
        String response;
        String[] temp;
        ArrayList<String> devolve = new ArrayList<String>();

        do{
            repeat = false;
            System.out.println(sentence);
            response = sc.nextLine();

            if (atLeastOneElement && response.equals("")){//Empty String, going to ask the user again
                System.out.println("Invalid input!");
                repeat = true;
            }else if(response.equals(""))
                return devolve;

        }while (repeat);

        temp = response.split(";");
        for (String aTemp : temp) {
            if (! devolve.contains(aTemp))
                devolve.add(aTemp);
        }

        return devolve;
    }

    ////
    //  Method responsible for asking the user information about the ideas
    ////
    private ArrayList<Integer> askIdeas(String sentence){
        String ideas;
        String[] temp;
        ArrayList<Integer> devolve = new ArrayList<Integer>();
        int pos = 0, temp_num;
        boolean repeat;

        do{
            repeat = false;
            System.out.println(sentence);
            ideas = sc.nextLine();

            if (ideas.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
                continue;
            }

            temp = ideas.split(";");

            for (String aTemp : temp) {
                try {
                    temp_num = Integer.parseInt(aTemp);
                    if(temp_num == -1)
                        return devolve;

                    else if(devolve.contains(temp_num)){
                        System.out.println("You have inserted the same idea twice, please enter again");
                        repeat = true;
                    }
                    devolve.add(temp_num);
                    pos = pos + 1;
                } catch (NumberFormatException n) {
                    System.out.println("Invalid input! Please enter again");
                    repeat = true;
                }
            }
        }while (repeat);

        return devolve;
    }
    ////
    //  Method responsible for collecting the information needed to create a new idea, and send a request to the TCP Server in
    //  order to create that new topic in the database
    ////
    private boolean createIdea(){
        String title, description, file, filePath;
        ArrayList<String> topics;
        ArrayList<Integer> ideasFor, ideasAgainst, ideasNeutral;
        int nshares = 1, price = 1, minNumShares = 1;
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
            System.out.println("Please enter the number of shares for the idea:");
            line = sc.nextLine();
            try{
                nshares = Integer.parseInt(line);
                if (nshares <= 0){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while(repeat);

        do{
            repeat = false;
            System.out.println("Please enter the price of each share of the idea:");
            line = sc.nextLine();
            try{
                price = Integer.parseInt(line);
                if (price <= 0){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while(repeat);

        do{
            repeat = false;
            System.out.println("Please enter the minimum number of shares you don't want to sell instantaneously for the given idea:");
            line = sc.nextLine();
            try{
                minNumShares = Integer.parseInt(line);
                if (minNumShares<0 || minNumShares>nshares){
                    System.out.println("Invalid number!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid number!");
                repeat = true;
            }
        }while(repeat);

        topics = askTopics("Please enter the titles of the topics where you want to include your idea (USAGE: topic1;topic2)",true);

        do{
            ideasFor = askIdeas("Is your idea in favor other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            ideasAgainst = askIdeas("Is your idea against other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            ideasNeutral = askIdeas("Is your idea neutral to other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            //repeat = checkIdeasRelations(ideasFor,ideasAgainst,ideasNeutral);
            if (!repeat)
                System.out.println("\nInvalid selection of the ideas' relations!Please repeat the selection!\n");
        }while (!repeat);

        do{
            repeat = false;
            System.out.println("Do you want to attach a file?(Y/N)");
            file = sc.nextLine();
            if (file.equals("Y") || file.equals("y")){
                System.out.println("Please enter the path to the file you want to attach:");
                filePath = sc.nextLine();
                try {
                    ficheiro = new NetworkingFile(filePath);
                } catch (FileNotFoundException e) {
                    System.out.println("Invalid file path!");
                    repeat = true;
                }
            }
            else if(!file.equals("N") && !file.equals("n")){
                repeat = true;
                System.out.println("Invalid input!");
            }
        }while (repeat);

        return conn.createIdea(title, description,nshares,price,topics,minNumShares,ideasFor,ideasAgainst,ideasNeutral,ficheiro);
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
        System.out.println("1 - Check a topic");//List all topics and choose one. While "inside" a topic list all ideas
        System.out.println("2 - Create a new topic");
        System.out.println("3 - Submit an idea");
        System.out.println("4 - Delete an idea");
        System.out.println("5 - Show Transaction History");
        System.out.println("6 - View Idea");
        System.out.println("7 - Search Topic");
        System.out.println("8 - Manage User Ideas");
        System.out.println("9 - Add relation between two ideas");
        System.out.println("10 - Buy shares of an idea");
        System.out.println("0 - Sair");

        do{
            repeat = false;
            System.out.print("Your choice: ");
            try{
                line = sc.nextLine();
                choice = Integer.parseInt(line);
                if(choice < 0 || choice > 10)
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
                System.out.println("Login unsucessfull!\nIf you want to register just enter 2, otherwise press any key to login again");
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
        //Lista ideias das quais o user nao detem 100% das shares
        ArrayList<Idea> canBuyIdeas = conn.getIdeasBuy();//List of ideas the user can buy
        boolean repeat;
        String line;
        int iid = -1, price = 1, numberSharesToBuy = 1, index = -1, minNumberShares = 0;

        if (canBuyIdeas==null || canBuyIdeas.size() == 0){
            System.out.println("There are no ideas to sell");
            return false;
        }

        System.out.println("List of ideas the user can buy:");
        for (Idea canBuyIdea : canBuyIdeas) System.out.println(canBuyIdea);

        //Pedir ao user que ideias quer comprar
        do{
            repeat = false;
            System.out.println("Please insert the id of the idea whose shares you want to buy: (If you want to go back please enter -1");
            line = sc.nextLine();
            try{
                iid = Integer.parseInt(line);
                if (iid == -1)
                    return false;
                index = hasElement(canBuyIdeas,iid);
                if (iid < 1 || index == -1){
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
            try{
                numberSharesToBuy = Integer.parseInt(line);
                if (canBuyIdeas.get(index).getSharesBuy() < numberSharesToBuy){
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
            System.out.println("Please insert the selling price of each share you are going to buy, or -1 if you dont" +
                    " want to change it:");
            line = sc.nextLine();
            try{
                price = Integer.parseInt(line);
                if(price < -1){
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
            System.out.println("Please insert the minimum number of shares you dont want to sell from the shares you " +
                    " are going to buy (If you already have shares and want to keep the same number enter -1)");
            line = sc.nextLine();
            try{
                minNumberShares = Integer.parseInt(line);
                if(minNumberShares < -1 || minNumberShares > numberSharesToBuy){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);


        return conn.buyShares(iid,numberSharesToBuy,price,minNumberShares);
    }

    private void mainLoop(){
        int choice, topic;
        boolean stay = true;

        while(stay){
            choice = Menu(sc);

            switch(choice){

                //Logout
                case 0:{
                    System.out.println("Thank you for posting with us, hope you have a nice day! Goodbye!");
                    System.exit(0);
                    break;
                }


                //Submit an idea
                case 3:{
                    if (!createIdea())
                        System.out.println("Error while creating an idea!");
                    else
                        System.out.println("Idea created with success");
                    break;
                }



                //Buy shares of an idea
                case 10:{
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

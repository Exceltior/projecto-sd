import java.io.FileNotFoundException;
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
        boolean repeat = false;

        do{
            System.out.println("Please enter the name of the topic:");
            nome = sc.nextLine();
            if (nome.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        repeat = false;
        do{
            System.out.println("Please enter the description of the topic:");
            descricao = sc.nextLine();
            if (descricao.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        return conn.createTopic(nome,descricao);
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
        for (int i=0;i<temp.length;i++){
            if (!devolve.contains(temp[i]))
                devolve.add(temp[i]);
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
        boolean repeat = false;

        do{
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
                    if(temp_num == -1) {
                        System.out.println("Okay then, no ideas, fucker!"); //FIXME
                        temp_num = -2;
                        return devolve;
                    }

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
    //  Prints the ideas in favour, against and neutral to a given idea
    ////
    private int printRelationsIdea(ArrayList<Integer> listIdeasIDs, int relationType){
        String temp;
        int iid = -2;
        Idea[] ideasList = null;

        if (relationType == 1)
            System.out.println("Please insert the id of the idea you want to see the ideas in favour:");
        else if(relationType == -1)
            System.out.println("Please insert the id of the idea you want to see the ideas against:");
        else if(relationType == 0)
            System.out.println("Please insert the id of the idea you want to see the ideas neutral:");

        try{
            temp = sc.nextLine();
            iid = Integer.parseInt(temp);
        }catch(NumberFormatException n){
            System.out.println("Invalid input!");
            return -1;
        }//We don't need to handle this exception

        if (listIdeasIDs.contains(iid)){
            //Get ideas
            ideasList = conn.getIdeaRelations(iid,relationType);

            if (relationType == 1)
                System.out.println("List of ideas in favour:");
            else if(relationType == -1)
                System.out.println("List of ideas against:");
            else if(relationType == 0)
                System.out.println("List of ideas neutral:");

            for (Idea anIdeasList : ideasList)
                System.out.println(anIdeasList);

            if(ideasList.length == 0)
                System.out.println("No ideas were found!");

            return ideasList.length;
        }

        return -1;
    }


    ////
    //  Provides a number of options to perform over a list of ideas
    ////
    private void ideaOptions(ArrayList<Integer> listIdeasIDs, ArrayList<Integer> ideasFilesListIds){
        boolean stay = true;
        String line, temp;
        int choice, iid, result;
        ArrayList<String> listTopicsNames;
        ClientTopic[] listTopics;


        while (stay){
            System.out.println("\nIdea Options:");
            System.out.println("1 - Comment an idea");
            System.out.println("2 - See ideas in favour of a given idea");
            System.out.println("3 - See ideas against a given idea");
            System.out.println("4 - See ideas neutral for the given idea");
            System.out.println("5 - Download file attached to an idea");
            System.out.println("0 - Go Back");
            System.out.print("Your choice: ");
            try{
                line = sc.nextLine();
                choice = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid option!");
                continue;
            }

            switch (choice){
                case 0:
                    stay = false;
                    break;

                case 1:{
                    //Comment an idea
                    listTopicsNames = new ArrayList<String>();

                    System.out.println("Please insert the id of the idea you want comment.");
                    try{
                        temp = sc.nextLine();
                        iid = Integer.parseInt(temp);
                    }catch(NumberFormatException n){
                        System.out.println("Invalid input!");
                        break;
                    }//We don't need to handle this exception

                    if (listIdeasIDs.contains(iid)){
                        //Going to get the idea's topic
                        listTopics = conn.getIdeaTopics(iid);

                        for (int i=0;i< listTopics.length;i++)
                            listTopicsNames.add(listTopics[i].getTitle());

                        commentIdea(listTopicsNames,iid);

                    }else
                        System.out.println("Error! Idea(s) not comented");
                    break;
                }

                case 2:{
                    //See ideas in favour
                    result = printRelationsIdea(listIdeasIDs,1);
                    if ( result == -1)
                        System.out.println("Error! Could not show ideas in favour");
                    else
                        stay = false;

                    break;
                }

                case 3:{
                    //See ideas against
                    result = printRelationsIdea(listIdeasIDs,-1);
                    if (result == -1 )
                        System.out.println("Error! Could not show ideas against");
                    else
                        stay = false;
                    break;
                }

                case 4:{
                    //See ideas neutral
                    result = printRelationsIdea(listIdeasIDs,0);
                    if (result == -1)
                        System.out.println("Error! Could not show ideas neutral");
                    else
                        stay = false;
                    break;
                }

                case 5: {
                    //Download file attached to an idea
                    if (ideasFilesListIds.size()>0){
                        downloadFile(ideasFilesListIds);
                    }
                    else
                        System.out.println("There are no ideas with files associated");

                    break;
                }

                default:{
                    System.out.println("Invalid option!");
                    break;
                }
            }
        }
    }

    ////
    //  Searchs an idea by its idea id and title
    ////
    private void searchIdea(){
        String temp, title;
        int iid = -2;
        boolean repeat;
        Idea[] userSelectedIdea;
        ArrayList<Integer> listIdeasIDs = new ArrayList<Integer>();//List of user's ideas ids
        ArrayList<Integer> listIdeasFilesListIDs = new ArrayList<Integer>();//List of ideas' ids with file

        System.out.println("\n\nWelcome to the Idea's Search Engine!\nWe provide two ways of searching for a topic:" +
                "By its name and by its topic id. You must insert at least one of these fields\n\n");

        do{
            repeat = false;
            System.out.println("Please enter the id of the idea you want to search. If you don't know the idea id just press 'ENTER'");
            temp = sc.nextLine();

            //if the user pressed the enter key then we just ignore the topic id
            if (!temp.equals("")){
                try{
                    iid = Integer.parseInt(temp);
                } catch(NumberFormatException n){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }
        }while (repeat);

        System.out.println("Please enter the title of the idea you want to search. If you don't know the idea title just press 'ENTER'");
        title = sc.nextLine();

        if (iid == -1 && title.equals("")){
            System.out.println("Idea id and/or title were not provided, no idea can be searched");
            return ;
        }

        userSelectedIdea = conn.getIdea(iid,title);

        if (userSelectedIdea == null){
            System.out.println("No idea was found!");
            return;
        }

        System.out.println("\nIdeas found:");
        for (int i=0;i<userSelectedIdea.length;i++){
            listIdeasIDs.add(userSelectedIdea[i].getId());
            System.out.println(userSelectedIdea[i]);
            if (userSelectedIdea[i].getFile().equals("Y")){
                 listIdeasFilesListIDs.add(userSelectedIdea[i].getId());
            }
        }

        ideaOptions(listIdeasIDs,listIdeasFilesListIDs);
    }

    ////
    // Searchs a topic by its topic id and name
    ////
    private void searchTopic(){
        String name, temp;
        int tid = -2;
        boolean repeat;

        System.out.println("\n\nWelcome to the Topic's Search Engine!\nWe provide two ways of searching for a topic:" +
                "By its name and by its topic id. You must insert at least one of these fields\n\n");

        do{
            repeat = false;
            System.out.println("Please enter the id of the topic you want to search. If you don't know the topic id just press 'ENTER'");
            temp = sc.nextLine();

            //if the user pressed the enter key then we just ignore the topic id
            if (!temp.equals("")){
                try{
                    tid = Integer.parseInt(temp);
                } catch(NumberFormatException n){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }
        }while (repeat);


        System.out.println("Please enter the name of the topic you want to search. If you don't know the topic name just press 'ENTER'");
        name = sc.nextLine();

        if (tid == -1 && name.equals("")){
            System.out.println("Topic id and/or name were not provided, no topic can be searched");
            return ;
        }

        ClientTopic userSelectedTopic = conn.getTopic(tid,name);

        if (userSelectedTopic == null){
            System.out.println("No topic was found!");
            return;
        }

        System.out.println("\nTopic found:\n" + userSelectedTopic);

        commentTopic(tid);
    }

    ////
    //  Method responsible for checking if the user hasn't selected the same idea for different relations
    ////
    private boolean checkIdeasRelations(ArrayList<Integer> ideasFor, ArrayList<Integer> ideasAgainst, ArrayList<Integer> ideasNeutral){

        for (Integer anIdeasFor : ideasFor) {
            if (ideasAgainst.contains(anIdeasFor) || ideasNeutral.contains(anIdeasFor)){
                System.out.println("Vou devolver false");
                return false;
            }
        }

        //Here we know that none of the items from ideasFor is in ideasAgainst nor in ideasNeutral
        for (Integer anIdeasAgainst : ideasAgainst){
            if (ideasNeutral.contains(anIdeasAgainst)){
                System.out.println("Vou devolver false2");
                return false;
            }
        }
        return true;
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
        boolean repeat = false;
        String line;

        do{
            System.out.println("Please enter the title of the idea:");
            title = sc.nextLine();
            if (title.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        repeat = false;

        do{
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
            repeat = checkIdeasRelations(ideasFor,ideasAgainst,ideasNeutral);
            if (!repeat)
                System.out.println("\nInvalid selection of the ideas' relations!Please repeat the selection!\n");
        }while (!repeat);

        repeat = false;
        do{
            System.out.println("Do you want to attach a file?(Y/N)");
            file = sc.nextLine();
            if (file.equals("Y") || file.equals("y")){
                System.out.println("Please enter the path to the file you want to attach:");
                filePath = sc.nextLine();
                try {
                    ficheiro = new NetworkingFile(filePath);
                    System.out.println("AQUI " + ficheiro == null);
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
    //  Creates a new idea, commenting directly on a topic
    ////
    private boolean commentIdea(ArrayList<String> topicTitle, int iid){
        String title, description, file, filePath, line;
        int commentType = -2, nshares = 1, price = 1, minNumShares = 1;
        ArrayList<String> topics;
        ArrayList<Integer> ideasFor, ideasAgainst, ideasNeutral;
        boolean repeat = false;
        NetworkingFile ficheiro = null;

        do{
            System.out.println("Please enter the title of the idea:");
            title = sc.nextLine();
            if (title.equals("")){
                System.out.println("Invalid input!");
                repeat = true;
            }
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
                if (nshares < 1){
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
            System.out.println("Please enter the price of each share of the idea:");
            line = sc.nextLine();
            try{
                price = Integer.parseInt(line);
                if (price < 1){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid input");
                repeat = true;
            }
        }while(repeat);

        do{
            System.out.println("Please enter the minimum number of shares you don't want to sell instantaneously for the given idea:");
            line = sc.nextLine();
            try{
                minNumShares = Integer.parseInt(line);
                if (minNumShares<0 || minNumShares>nshares){
                    System.out.println("Invalid input!");
                    repeat = true;
                }
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while(repeat);

        topics = askTopics("If you want to include this idea in other topics, please enter their titles (USAGE: topic1;topic2)\n" +
                "If you just want to include the idea on the current topic just press 'Enter'",false);

        for(int i=0;i<topicTitle.size();i++){
            if (!topics.contains(topicTitle.get(i)))
                topics.add(topicTitle.get(i));
        }

        //Ask relation type
        do{
            System.out.println("Please select the relantionship between the idea you choose and the one you are just going to create\n(USAGE: 1-> For; -1->Against; 0-> Neutral)");
            try{
                line = sc.nextLine();
                commentType = Integer.parseInt(line);
                if(commentType == 1 || commentType == -1 || commentType == 0)
                    repeat = false;
                else
                    repeat = true;
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while(repeat);

        do{
            ideasFor = askIdeas("Is your idea in favor other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            ideasAgainst = askIdeas("Is your idea against other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            ideasNeutral = askIdeas("Is your idea neutral to other ideas already stored in the system? If so, please enter the ids of the ideas (USAGE: iid1;iid2)\nEnter -1 to cancel");
            repeat = checkIdeasRelations(ideasFor,ideasAgainst,ideasNeutral);
            if (!repeat)
                System.out.println("\nInvalid selection of the ideas' relations!Please repeat the selection!\n");
        }while (!repeat);

        if (commentType == 1){
            if ( ideasFor == null){
                ideasFor = new ArrayList<Integer>();
                ideasFor.add(iid);
            }else if(!ideasFor.contains(iid))
                ideasFor.add(iid);
        }

        else if(commentType == -1){
            if ( ideasAgainst == null){
                ideasAgainst = new ArrayList<Integer>();
                ideasAgainst.add(iid);
            }else if(!ideasAgainst.contains(iid))
                ideasAgainst.add(iid);
        }

        else{
            if ( ideasNeutral == null){
                ideasNeutral = new ArrayList<Integer>();
                ideasNeutral.add(iid);
            }else if(!ideasNeutral.contains(iid))
                ideasNeutral.add(iid);
        }

        repeat = false;
        do{
            System.out.println("Do you want to attach a file?(Y/N)");
            file = sc.nextLine();
            if (file.equals("Y") || file.equals("y")){
                System.out.println("Please enter the path to the file you want to attach:");
                filePath = sc.nextLine();
                try {
                    ficheiro = new NetworkingFile(filePath);
                } catch (FileNotFoundException e) {
                    System.out.println("Invalid file path");
                    repeat = true;
                }
            }
            else if(!file.equals("N") && file.equals("n")){
                repeat = true;
                System.out.println("Invalid input");
            }
        }while (repeat);

        return conn.createIdea(title, description,nshares,price,topics,minNumShares,ideasFor,ideasAgainst,ideasNeutral,ficheiro);
    }

    ////
    //  Sets the price per share of a given idea
    ////
    private boolean setPriceShares(ArrayList<Integer> listUserIdeasIDs){
        int iid = -1, price = -1;
        String line, line2;

        //Ask which idea we would like to set the shares' prices
        do{
            System.out.println("Please insert the id of the idea you would like to see the shares' prices:");
            line = sc.nextLine();
            System.out.println("Please insert the new price per share you would like to set:");
            line2 = sc.nextLine();
            try{
                iid = Integer.parseInt(line);
                price = Integer.parseInt(line2);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                iid = -1;
            }
        }while(!listUserIdeasIDs.contains(iid));

        return conn.setPriceShares(iid,price);
    }

    ////
    //  Shows the user all its shares of every idea he/she has and its price
    ////
    private void showPriceShares(ArrayList<Integer> listUserIdeasIDs){
        int iid = -2;
        String line;
        String[] pricesShares;

        //Ask which idea we would like to see the shares' prices
        do{
            System.out.println("Please insert the id of the idea you would like to see the shares' prices:");
            line = sc.nextLine();
            try{
                iid = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                iid = -2;
            }
        }while(!listUserIdeasIDs.contains(iid));

        pricesShares = conn.showPricesShares(iid);

        if (pricesShares == null || pricesShares.length == 0)
            System.out.println("The user doesnt have any share for this idea");//FIXME: This should never happen right?
        else{
            //Print the shares' information
            System.out.println("Share{iid = " + pricesShares[0] + ",Number of Shares = " + pricesShares[1] +
                    ",Value = " + pricesShares[2] + ", Minimum Number of Shares = " + pricesShares[3] + "}");
        }
    }

    ////
    //  Method that shows the number of shares not to sell instantaneously of a given idea
    ////
    private void checkSharesNotSell(ArrayList<Integer> listUserIdeasIDs){
        int iid = -2, shares = -1;
        String line;

        do{
            System.out.println("Please insert the id of the idea whose number of shares not to sell instantaneously " +
                    "you would like to check:");
            line = sc.nextLine();
            try{
                iid = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                iid = -2;
            }
        }while (!listUserIdeasIDs.contains(iid));

        shares = conn.getSharesNotSell(iid);
        if (shares >= 0)
            System.out.println("The number of shares not to sell instantaneously of the idea " + iid + " for this user is " + shares);
        else
            System.out.println("Error while getting information!");
    }

    ////
    //  Method that sets the number of shares not to sell instantaneously of a given idea owned (totally or partially) by
    //  a given user
    ////
    private void setSharesNotSell(ArrayList<Integer> listUserIdeasIDs){
        int numberShares, iid = -2, numberSharesIdea = -2;
        boolean repeat;
        String line;
        String[] priceShares;

        do{
            System.out.println("Please insert the id of the idea whose number of shares no to sell instantaneously you " +
                    "want to update:");
            line = sc.nextLine();
            try{
                iid = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                iid = -2;
            }
        }while (!listUserIdeasIDs.contains(iid));

        do{
            repeat = false;
            System.out.println("Please enter the new number of shares you dont want to sell instantaneously:");
            line = sc.nextLine();
            try{
                numberShares = Integer.parseInt(line);

                //Get the number of shares for the given idea
                priceShares = conn.showPricesShares(iid);
                numberSharesIdea = Integer.parseInt(priceShares[1]);

                if(numberShares > numberSharesIdea){
                    System.out.println("Invalid number of shares!");
                    repeat = true;
                }

            }catch (NumberFormatException n){
                System.out.println("Invalid input!");
                numberShares = -2;
            }
        }while (repeat);

        if (conn.setSharesNotSell(iid,numberShares))
            System.out.println("Number of shares no to sell instantaneously updated with success!");
        else
            System.out.println("Error while updating number of shares no to sell instantaneously!");
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
        String line;
        boolean repeat;

        System.out.println("\n\nMain Menu");
        System.out.println("1 - Check a topic");//List all topics and choose one. While "inside" a topic list all ideas
        System.out.println("2 - Create a new topic");
        System.out.println("3 - Submit an idea");
        System.out.println("4 - Delete an idea");
        System.out.println("5 - Show Transaction History");
        System.out.println("6 - Search Idea");
        System.out.println("7 - Search Topic");
        System.out.println("8 - Manage User Ideas");
        System.out.println("9 - Add relation between two ideas");
        System.out.println("10 - Buy shares of an idea");
        System.out.println("0 - Sair");

        do{
            repeat = false;
            System.out.print("Your choice: ");

        ////
        //  FIXME: INCOMPLETE, THERE ARE OPTIONS MISSING!!!!!!!
        ////
            try{
                line = sc.nextLine();
                choice = Integer.parseInt(line);
                if(choice < 0 || choice > 9)
                    repeat = true;
            }catch(NumberFormatException n){
                repeat = true;
            }

        }while(repeat);

        return choice;
    }

    ////
    //  Display the Account Settings Menu
    ////
    private void manageUserIdeas(){
        int option = -1, choice = -2;
        String line;
        Idea[] listIdeas;
        //List of ideas owned by the user (totally or partially)
        ArrayList<Integer> listUserIdeasIDs = new ArrayList<Integer>();
        //List of ideas owned by the user (totally or partially) that have files associated
        ArrayList<Integer> ideasFilesListIds = new ArrayList<Integer>();


        //Display user ideas
        listIdeas = conn.getIdeasFromUser();

        System.out.println("\n\nUser Ideas List:");
        for (int i=0;i<listIdeas.length;i++){
            System.out.println(listIdeas[i]);
            listUserIdeasIDs.add(listIdeas[i].getId());
            if (listIdeas[i].getFile().equals("Y"))
                ideasFilesListIds.add(listIdeas[i].getId());
        }

        do{
            System.out.println("\n\n             Manage User Ideas");
            System.out.println("1 - Check idea's shares prices");
            System.out.println("2 - Set idea's shares prices");
            System.out.println("3 - Check idea's number of shares not to sell instantaneously");
            System.out.println("4 - Set idea's number of shares not to sell instantaneously");
            System.out.println("5 - Check ideas's relantionships");
            System.out.println("6 - Download file associated with an idea");
            System.out.println("0 - Return to Main Menu");
            System.out.print("Your option: ");
            try{
                line = sc.nextLine();
                option = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                option = -1;
            }
        }while (option<0 || option>6);

        switch(option){

            case 1:{
                showPriceShares(listUserIdeasIDs);
                break;
            }

            case 2:{
                //FIXME FIXME FIXME VER COM O MAXI SISTEMA NOTIFICAÇOES
                if(setPriceShares(listUserIdeasIDs))
                    System.out.println("Operation completed with success!");
                else
                    System.out.println("Operation could not be completed");
                break;
            }

            case 3:{
                //Check idea's shares not to sell instantaneously
                checkSharesNotSell(listUserIdeasIDs);
                break;
            }

            case 4:{
                //Set idea's shares not to sell instantaneously
                setSharesNotSell(listUserIdeasIDs);
                //FIXME FIXME FIXME VER COM O MAXI SISTEMA NOTIFICAÇOES
                break;
            }

            case 5:{
                //Pedir ideia e depois listar todas as relacoes
                //FIXME FIXME FIXME FAZER FUNCAO BONITINHA PARA ISTO
                do{
                    System.out.println("Please insert the idea id whose relations you want to check:");
                    try{
                        line = sc.nextLine();
                        choice = Integer.parseInt(line);
                    }catch(NumberFormatException n){
                        System.out.println("Invalid option");
                        choice = -2;
                    }
                }while (!listUserIdeasIDs.contains(choice));

                //Print Ideas in Favour
                listIdeas = conn.getIdeaRelations(choice,1);

                if (listIdeas.length > 0){
                    System.out.println("List of ideas in favour:");
                    for (Idea anIdeasList : listIdeas)
                        System.out.println(anIdeasList);
                }
                else
                    System.out.println("There are no ideas in favour!");

                //Print Ideas in Against
                listIdeas = conn.getIdeaRelations(choice,-1);

                if (listIdeas.length > 0){
                    System.out.println("List of ideas in against:");
                    for (Idea anIdeasList : listIdeas)
                        System.out.println(anIdeasList);
                }
                else
                    System.out.println("There are no ideas against!");

                //Print Ideas Neutral
                listIdeas = conn.getIdeaRelations(choice,0);

                if (listIdeas.length > 0){
                    System.out.println("List of ideas in neutral:");
                    for (Idea anIdeasList : listIdeas)
                        System.out.println(anIdeasList);
                }
                else
                   System.out.println("There are no ideas neutral!");

                break;
            }

            case 6:{
                if (ideasFilesListIds.size()>0)
                    downloadFile(ideasFilesListIds);
                else
                    System.out.println("There are no ideas with files associated!" +
                            "");
                break;
            }

            default://Go back to Main Menu
                break;
        }
    }

    private void execute(){
        int choice;
        int  login_result = 3;
        boolean stay = true;

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
                } catch (InterruptedException e) {
                    System.err.println("Client thread was interrupted");
                    //FIXME: WHAT TO DO WITH THIS EXCEPTION????
                }
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
                        System.out.print("Registration unsucessfull :(\n1-Try login in with another username\n2-Try registration again\nYour choice: ");
                        choice = sc.nextInt();
                        sc.nextLine();//Clear the buffer
                        stay = true;
                    } while (choice!=1 && choice!=2);

                    //if (choice==1 || choice==2)
                    continue;
                }
                else{ //Now that the registration is sucessfull is time to login
                    System.out.println("Registration sucessfull");
                    stay = false;
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

        //  Login was successfull
        System.out.println("Login Successfull!");

        mainLoop();
    }

    private boolean deleteIdea(){
        int iid = -2;
        String line;
        boolean repeat = false;

        do{
            System.out.println("Please insert the id of the idea you want to delete:");
            line = sc.nextLine();
            try{
                iid = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                repeat = true;
            }
        }while (repeat);

        return conn.deleteIdea(iid);
    }

    private void downloadFile(ArrayList<Integer> ideasListIds){
        String sentence;
        int ideaFile = -1;
        NetworkingFile ficheiro;

        System.out.println("If you want to download a file from any idea listed please insert its id. Otherwise just press any key");
        try{
            sentence = sc.nextLine();
            ideaFile = Integer.parseInt(sentence);
        }catch(NumberFormatException n){}

        if (ideasListIds.contains(ideaFile)){
            //Download file
            ficheiro = conn.getIdeaFile(ideaFile);

            //Write file to disk
            if (ficheiro != null){
                try {
                    ficheiro.writeTo("./" + ficheiro.getName());
                } catch (FileNotFoundException e) {
                    System.out.println("Error while writing file to disk");
                }
            }
        }

        else
            System.out.println("No file is going to be downloaded");
    }

    private void commentTopic(int topic){
        Idea[] ideasList = conn.getTopicIdeas(topic);//Already says if has file or not
        ArrayList<Integer> ideasFilesListIds = new ArrayList<Integer>();
        String sentence;
        int iid = -1;

        System.out.println("\nList of Ideas for the given topic:\n");
        for (Idea anIdeasList : ideasList){
            System.out.println(anIdeasList);
            if (anIdeasList.getFile().equals("Y"))
                ideasFilesListIds.add(anIdeasList.getId());
        }

        //Ask to download files, if there are ideas with files
        if (ideasFilesListIds.size()>0){
            downloadFile(ideasFilesListIds);
        }

        //Now we are going to ask the user if he wants to create an idea
        ClientTopic[] temp = conn.getTopics();

        ArrayList<String> topicName = new ArrayList<String>();
        topicName.add(temp[topic - 1].getTitle());

        System.out.println("If you want to comment an idea, please insert its id. Otherwise just press any key");
        try{
            sentence = sc.nextLine();
            iid = Integer.parseInt(sentence);
        }catch(NumberFormatException n){}//We don't need to handle this exception

        if (iid == -1)
            return ;

        if(!commentIdea(topicName,iid))
            System.err.println("Idea not commented");
        else
            System.out.println("Idea commented with success\n");
    }

    private int listTopics(){
        int selected, min_id_topic = 0, max_id_topic = 0;
        ClientTopic[] topics = conn.getTopics();
        String line;

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
                line = sc.nextLine();
                selected = Integer.parseInt(line);
            }catch(InputMismatchException m){
                selected = -1;
            }
        }while (selected < min_id_topic || selected > max_id_topic);
        return selected;
    }

    private void setRelationIdeas(){
        int iidparent = -2, iidchild = -2, type = -2;
        String line;
        boolean repeat = true;

        while (repeat){
            System.out.println("Please select the id for the first idea in the relationship:");
            try{
                line = sc.nextLine();
                iidparent = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                continue;
            }
            System.out.println("Please select the id for the second idea in the relationship:");
            try{
                line = sc.nextLine();
                iidchild = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
                continue;
            }

            System.out.println("Please insert the type of the relationship (1->For;-1->Against;0->Neutral)");
            try{
                line = sc.nextLine();
                type = Integer.parseInt(line);
            }catch(NumberFormatException n){
                System.out.println("Invalid input!");
            }

            if (type == 1 || type == 0 || type == -1)//Valid option
                repeat = false;
            else
                repeat = true;
        }

        if (conn.setRelationBetweenIdeas(iidparent,iidchild,type))
            System.out.println("Relationship between ideas successfully!");
        else
            System.out.println("Relationship between ideas was not added");
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
        for (int i=0;i<canBuyIdeas.size();i++)
            System.out.println(canBuyIdeas.get(i));

        //Pedir ao user que ideias quer comprar
        do{
            repeat = false;
            System.out.println("Please insert the id of the idea whose shares you want to buy:");
            line = sc.nextLine();
            try{
                iid = Integer.parseInt(line);
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
        int choice, topic, idea;
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

                //Check a topic - List all the topics and ask the user which one he wants. While "inside" a topic list all ideas
                case 1:{
                    topic = listTopics();
                    commentTopic(topic);

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
                        System.out.println("Error while creating an idea!");
                    else
                        System.out.println("Idea created with success");
                    break;
                }

                //Delete an idea
                case 4:{
                    if (!deleteIdea())
                        System.out.println("Error while deleting the idea!");
                    else
                        System.out.println("Idea deleted with success");
                    break;
                }

                //Show Transaction History
                case 5:{
                    String[] history = conn.showHistory();

                    if (history == null || history.length == 0)
                        System.out.println("There are no previous transactions registered for this user");
                    else{
                        for (String aHistory : history)
                            System.out.println(aHistory);
                    }
                    break;
                }

                //Search idea
                case 6:{
                    searchIdea();
                    break;
                }

                //Search topic
                case 7:{
                    searchTopic();
                    break;
                }

                //Account Settings
                case 8:{
                    manageUserIdeas();
                    break;
                }

                //Add Relation Between ideas
                case 9:{
                    setRelationIdeas();
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
        Client client = new Client();
        client.execute();
        System.out.println("Vou sair do main");
    }
}

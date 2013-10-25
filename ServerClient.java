import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;

////
// This class, which implements an independent thread, is responsible for handling all requests from a given client
// (given to us referenced by its socket)
//
public class ServerClient implements Runnable {
    private Socket socket = null;
    private DataOutputStream outStream = null;
    private DataInputStream inStream = null;

    private RMIConnection connection;
    private Server server;

    // The client's uid. -1 means not logged in.
    private int uid = -1;

    static int limit_characters_topic = 20;//Number of characters for the topic's name

    public ServerClient(Socket currentSocket, RMIConnection connection, Server server) {
        this.socket = currentSocket;
        this.connection = connection;
        this.server = server;
        try {
            this.outStream = new DataOutputStream(currentSocket.getOutputStream());
            this.inStream = new DataInputStream(currentSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error constructing a new ServerClient (did the connection die?");
        }

        if(!initRMIConnection()) {
            System.err.println("Error getting the RMI connection!");
        }
    }

    private boolean initRMIConnection() {

        connection.waitUntilRMIIsUp();

        // Should never return false!
        return connection.getRMIInterface() != null;
    }

    private boolean isLoggedIn() {
        return uid != -1;
    }


    @Override
    public void run() {

        if ( !server.isPrimary() ) {
            // We don't even care if the message goes out!
            Common.sendMessage(Common.Message.ERR_NOT_PRIMARY,outStream);
            try {
                socket.close();
            } catch (IOException ignored)
            {} // We don't care if the uer didn't get the ERR_NOT_PRIMARY message
            server.removeSocket(socket);
            return ;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream)) {
            System.out.println("Connection to a client dropped while starting!");
            server.removeSocket(socket);
            return;
        }

        for(;;) {
            Common.Message msg;

            // Read the next Message/Request
            if ( ( msg = Common.recvMessage(inStream)) == Common.Message.ERR_NO_MSG_RECVD){
                System.out.println("Error No Message Received!!!");
                break ;
            }

            if ( isLoggedIn() ) {
                try {
                    connection.getRMIInterface().updateUserTime(uid);
                } catch (RemoteException e) {
                    connection.onRMIFailed();
                    server.removeSocket(socket);
                    System.err.println("RMI exception here!"); //FIXME: Do the retry mechanism?
                    break;
                }
            }


            // Handle the request
            // FIXME: All of these prints are mostly here just for debugging. In practice, they will mean that we've
            // lost the connection to che client. We should drop them in production code
            if ( msg == Common.Message.REQUEST_LOGIN) {
                if ( !handleLogin() ){
                    System.err.println("Error in the handle login method!!!");
                    break ;
                }
            } else if (msg == Common.Message.REQUEST_REG){
                if (!handleRegistration()){
                    System.err.println("Error in the handle registration method!!!");
                    break ;
                }
            } else if ( msg == Common.Message.REQUEST_GETTOPICS){
                if ( !handleListTopicsRequest() ){
                    System.err.println("Error in the handle list topics request method!!!");
                    break ;
                }
            } else if (msg == Common.Message.REQUEST_CREATETOPICS){
                if ( !handleCreateTopicRequest() ){
                    System.err.println("Error in the handle create topics request method!!!");
                    break ;
                }
            }else if (msg == Common.Message.REQUEST_GET_IDEA_BY_IID){
                if ( !handleGetIdeaByIID() ){
                    System.err.println("Error in the handle get idea by IID method!!!");
                    break ;
                }
            }else if(msg == Common.Message.REQUEST_CREATEIDEA){
                if ( !handleCreateIdea()){
                    System.err.println("Error in the handle create idea method!!!");
                    break ;
                }
            }else if (msg == Common.Message.REQUEST_GETTOPICSIDEAS){
                if (!handleGetTopicsIdea()){
                    System.err.println("Error in the handle get topics idea method!!!");
                    break;
                }
            }else if( msg == Common.Message.REQUEST_GET_HISTORY){
                if (!handleGetHistory()){
                    System.err.println("Error in the handle get history method!!!!!");
                    break;
                }
            } else if ( msg == Common.Message.REQUEST_DELETE_IDEA) {
                if (!handleDeleteIdea()) {
                    System.err.println("Error deleting idea");
                    break;
                }
            }else if( msg == Common.Message.REQUEST_GETTOPIC){
                if (!handlegetTopic()){
                    System.err.println("Error in the handle get topic method!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_GET_IDEA){
                if (!handleGet_Idea()){
                    System.err.println("Error in the handle get idea method!!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_GET_TOPICS_OF_IDEA){
                if (!handleGetTopicsOfIdea()){
                    System.err.println("Error in the handle get topics of idea method!!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_GETUSERIDEAS){
                if (!handleGetUserIdeas()){
                    System.err.println("Error in the handle get user ideas method!!!!!!");
                    break;
                }
            }else if(msg == Common.Message.REQUEST_SETIDEARELATION){
                if (!handleSetIdeaRelationship()){
                    System.err.println("Error in the handle set idea relationship method!!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_GETIDEASFAVOUR){
                if (!handleGetIdeasRelation(1)){
                    System.err.println("Error in the handle get ideas relation 1 method!!!!!!");
                    break;
                }
            }else if(msg == Common.Message.REQUEST_GETIDEASAGAINST){
                if (!handleGetIdeasRelation(-1)){
                    System.err.println("Error in the handle get ideas relation -1 method!!!!!!");
                    break;
                }
            }else if(msg == Common.Message.REQUEST_GETIDEASNEUTRAL){
                if (!handleGetIdeasRelation(0)){
                    System.err.println("Error in the handle get ideas relation 0 method!!!!!!");
                    break;
                }
            }else if(msg == Common.Message.REQUEST_GETIDEASHARES){
                if (!handleGetIdeaShares()){
                    System.err.println("Error in the handle get idea shares method!!!!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_SETPRICESHARES){
                if (!handleSetPriceShares()){
                    System.err.println("Error in the handle get idea shares method!!!!!!!!");
                    break;
                }
            }else if(msg == Common.Message.REQUEST_GETSHARESNOTSELL){
                if(!handleGetSharesNotSell()){
                    System.err.println("Error in the handle get shares not sell method!!!!!");
                    break;
                }
            }else if(msg == Common.Message.REQUEST_SETSHARESNOTSELL){
                if (!handleSetSharesNotSell()){
                    System.err.println("Error in the handle set shares not sell method!!!!!");
                    break;
                }
            }else if(msg == Common.Message.REQUEST_GETFILE){
                if (!handleGetFile()){
                    System.err.println("Error in the handle get file method!!!!!");
                    break;
                }
            }else if(msg == Common.Message.REQUEST_GET_IDEAS_FILES){
                if (!handleGetIdeasFiles()){
                    System.err.println("Error in the handle get ideas files method!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_GET_IDEA_FILE){
                if (!handleGetIdeaFile()){
                    System.err.println("Error in the handle get idea file method!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_GET_IDEAS_BUY){
                if (!handleGetIdeasCanBuy()){
                    System.err.println("Error in the handle get ideas can buy method!!!!!");
                    break;
                }
            }else if (msg == Common.Message.REQUEST_BUYSHARES){
                if (!handleBuyShares()){
                    System.err.println("Error in the handle buy shares method!!!!!");
                    break;
                }
            }
        }

        ////
        //  FIXME: Quando não há topicos para mostrar (porque não há nada na base de dados) tu simplesmente assumes que
        //  houve mega bode na ligação e assumes que o cliente se desligou, porque não dizer simplesmente ao utilizador
        //  "Amigo, não há tópicos para mostrar queres adicionar algum??"
        ////
        if ( !isLoggedIn() )
            System.out.println("Connection to UID "+uid+" dropped!");
        else
            System.out.println("Connection to a client dropped!");
        server.removeSocket(socket);
    }

    private boolean handleRegistration(){
        String user, pass, date, email;
        boolean registration; // true = managed to register, false = problems registering

        if ( (user = Common.recvString(inStream)) == null)
            return false;
        if ( (pass = Common.recvString(inStream)) == null)
            return false;
        if ( (email = Common.recvString(inStream)) == null)
            return false;
        if ( (date = Common.recvString(inStream)) == null)
            return false;

        System.out.println("Vou registar:" +  user + " " + pass + " " + email + " " + date);


        ArrayList<Object> objects = new ArrayList<Object>(); objects.add(user); objects.add(pass); objects
                .add(email); objects.add(date);
        Request registerUserRequest = new Request(-1, Request.RequestType.REGISTER_USER,objects);
        server.queue.enqueue(registerUserRequest);
        registerUserRequest.waitUntilDispatched();
        registration = (Boolean)registerUserRequest.requestResult.get(0);
        // We don't need to dequeue it, because since it's a registration request it's done automagically!

        if (registration){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
        } else {
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;
            else
                System.out.println("Foi enviada mensagem de erro");
            // Here we have to return true to keep the connection to the client alive
            return true;
        }

        System.out.println("O handle registration correu impecavelmente bem e o cliente foi registado com sucesso :)");

        return true;
    }


    ////
    //  Creates a new topic
    ////
    private boolean handleCreateTopicRequest(){
        String nome, descricao;
        boolean result;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( (nome = Common.recvString(inStream)) == null)
            return false;

        if ( (descricao = Common.recvString(inStream)) == null)
            return false;

        Request createTopicsRequest;
        if ( (createTopicsRequest = server.queue.getFirstRequestByUIDAndType(uid,Request.RequestType.CREATE_TOPIC))
                == null) {
            ArrayList<Object> objects = new ArrayList<Object>(); objects.add(nome); objects.add(descricao); objects
                    .add(this.uid);
            createTopicsRequest = new Request(uid, Request.RequestType.CREATE_TOPIC,objects);
            server.queue.enqueue(createTopicsRequest);
        }
        createTopicsRequest.waitUntilDispatched();
        result = (Boolean)createTopicsRequest.requestResult.get(0);

        if (result){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
        } else {
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;
        }

        server.queue.dequeue(createTopicsRequest);
        return true;
    }

    ////
    //  Get the list of topics where a given idea is
    ////
    private boolean handleGetTopicsOfIdea(){
        int iid;
        ServerTopic[] list_topics;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        try{
            list_topics = connection.getRMIInterface().getIdeaTopics(iid);
        }catch(RemoteException r){
            System.err.println("Error while getting ideas from topic");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (!Common.sendInt(list_topics.length,outStream))
            return false;

        for (ServerTopic list_topic : list_topics) {
            if (! list_topic.writeToDataStream(outStream))
                return false;
        }

        if (!Common.sendMessage(Common.Message.MSG_OK,outStream))
            return false;

        return true;
    }

    ////
    // Get the list of ideas in a given topic
    ////
    private boolean handleGetTopicsIdea(){
        int topicid;
        Idea[] ideaslist;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if ( (topicid = Common.recvInt(inStream)) == -1)
            return false;

        //Now we go to the database and get the ideas

        try{
            ideaslist = connection.getRMIInterface().getIdeasFromTopic(topicid);

            if (ideaslist == null){
                if(!Common.sendInt(0,outStream))
                    return false;
            }

            else{
                if (!Common.sendInt(ideaslist.length,outStream))
                    return false;

                //Send ideas
                for (int i=0;i<ideaslist.length;i++){
                    if(!ideaslist[i].writeToDataStream(outStream))
                        return false;
                }
            }

            if (!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;

        }catch (RemoteException r){
            System.err.println("Error while getting ideas from topic");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        return true;
    }

    private String[] receiveData(){
        String[] data;
        int numIdeas;
        String temp;

        if ( (numIdeas = Common.recvInt(inStream)) == -2)
            return new String[0];

        data = new String[numIdeas];
        for (int i=0;i<numIdeas;i++){
            if( (temp = Common.recvString(inStream)) == null)
                return null;
            data[i] = temp;
        }

        return data;
    }

    private boolean receiveInt(ArrayList<Integer> ideias){
        int[] data;
        int numIdeas, temp;

        if ( (numIdeas = Common.recvInt(inStream)) == -1) {
            return false;
        }

        for (int i=0;i<numIdeas;i++){
            if( (temp = Common.recvInt(inStream)) == -1)
                return false;
            ideias.add(temp);
        }

        return true;
    }

    private boolean handleBuyShares(){
        int iid, price, numberSharesToBuy, minNumberShares, numSharesAlreadyHas, numMinSharesAlreadyHas;
        boolean check = false;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Receive Data
        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        if ( (numberSharesToBuy = Common.recvInt(inStream)) == -1)
            return false;

        if ( (price = Common.recvInt(inStream)) == -1)
            return false;

        if (price == -2)//To void the "They're trying to hack us" message
            price = -1;

        if ( (minNumberShares = Common.recvInt(inStream)) == -1)
            return false;

        if (minNumberShares == -2)//To avoid the "They're trying to hack us" message
            minNumberShares = -1;

        try{
            //1º - Obter numero de shares que o user ja tem para ver com quantas e que ele vai ficar;
            Share currentShares = connection.getRMIInterface().getSharesIdeaForUid(iid,uid);
            if ( currentShares != null ) {
                numSharesAlreadyHas = currentShares.getNum();
            } else {
                numSharesAlreadyHas = 0;
            }
            System.out.println(uid+", "+iid+", "+numSharesAlreadyHas +", "+price+", " +
                    ""+minNumberShares);
             check = connection.getRMIInterface().registerGetSharesRequest(uid,iid,(numberSharesToBuy+numSharesAlreadyHas),price,
                     minNumberShares);
        }catch(RemoteException r){
            System.err.println("Error in the handle buy shares!");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (check){
            if (!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;
        } else {
            if(!Common.sendMessage(Common.Message.MSG_ERR,outStream))
                return false;
        }

        return true;
    }

    private boolean setRelations(int iid,ArrayList<Integer> ideas, int relationType, ArrayList<Request> requests,
                                 int oldI){
        for (int i=0;i<ideas.size();i++) {
            boolean result;
            Request setIdeasRelationsRequest = null;

            if ( (setIdeasRelationsRequest = server.queue.getNthRequestByUIDAndType(uid,
                    Request.RequestType.SET_IDEAS_RELATIONS, oldI+i+1)) ==
                    null) {

                ArrayList<Object> objects = new ArrayList<Object>(); objects.add(iid); objects.add(ideas.get(i)); objects
                        .add(relationType);
                setIdeasRelationsRequest = new Request(uid, Request.RequestType.SET_IDEAS_RELATIONS,objects);
                server.queue.enqueue(setIdeasRelationsRequest);
            }

            requests.add(setIdeasRelationsRequest);
            setIdeasRelationsRequest.waitUntilDispatched();

            result = (Boolean)setIdeasRelationsRequest.requestResult.get(0);
            System.out.println("IID +"+iid+" ideas[i]: "+ideas.get(i)+" relationType: "+relationType+" i: "+i+
            " oldI:" +  oldI + "result: "+ result); //FIXME
            if (!result) {
                //Alert the client that the idea is not valid
                if (!Common.sendMessage(Common.Message.ERR_NO_SUCH_IID, outStream))
                    return false;
            } else {
                if (!Common.sendMessage(Common.Message.MSG_OK, outStream))
                    return false;
            }
        }

        return true;
    }

    private boolean handleGetIdeasCanBuy(){
        ArrayList<Idea> ideasUserCanBuy = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        try{
             ideasUserCanBuy = connection.getRMIInterface().getIdeasCanBuy(uid);
        }catch(RemoteException r){
            System.err.println("Error while getting ideas the user can buy");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (ideasUserCanBuy == null){
            Common.sendMessage(Common.Message.MSG_ERR,outStream);
            return false;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Send Data
        if (!Common.sendInt(ideasUserCanBuy.size(),outStream))
            return false;

        for (int i=0;i<ideasUserCanBuy.size();i++){
            if (!ideasUserCanBuy.get(i).writeToDataStream(outStream))
                return false;
        }

        //Final confirmation
        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        return true;
    }

    ////
    //  Gets the ideas' relation where iidpai is given and the type is a parameter
    ////
    private boolean handleGetIdeasRelation(int type){
        int iid = -1;
        Idea[] ideasList;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        //Confirm data
        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        try{
             ideasList = connection.getRMIInterface().getIdeaRelations(iid,type);
        }catch(RemoteException r){
            System.err.println("RemoteException in the handleGetIdeasRelation method");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (ideasList == null){
            if (!Common.sendInt(0,outStream))
                return false;
        }

        else{
            //Send data
            if (!Common.sendInt(ideasList.length,outStream))
                return false;

            for (int i=0;i<ideasList.length;i++){
                if (!ideasList[i].writeToDataStream(outStream))
                    return false;
            }

        }

        //Final confirmation
        return Common.sendMessage(Common.Message.MSG_OK, outStream);

    }

    ////
    //  Sends a list of the ideas associated with a given user
    ////
    private boolean handleGetUserIdeas(){
        Idea[] userIdeas;
        int numUserIdeas;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        try{
             userIdeas = connection.getRMIInterface().getIdeasFromUser(uid);
        }catch (RemoteException r){
            System.err.println("Error while getting user's ideas");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (userIdeas == null){
            Common.sendMessage(Common.Message.ERR_IDEAS_NOT_FOUND,outStream);
            System.out.println("Error");
            return false;
        }

        else{
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        }

        if (!Common.sendInt(userIdeas.length,outStream))
            return false;

        for (int i=0;i<userIdeas.length;i++){
            if(!userIdeas[i].writeToDataStream(outStream))
                return false;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        return true;
    }


    ////
    //  Creates a new Idea
    ////
    private boolean handleCreateIdea(){
        Common.Message reply;
        String title, description, topic;
        String[] topicsArray;
        //int[] ideasForArray, ideasAgainstArray, ideasNeutralArray;
        ArrayList<Integer> ideasForArray = new ArrayList<Integer>(),
                           ideasAgainstArray = new ArrayList<Integer>(),
                           ideasNeutralArray = new ArrayList<Integer>();
        int nshares, price, result, numMinShares;
        boolean result_topics = false, result_shares;
        NetworkingFile ficheiro = null;
        Request addFileRequest = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
           return false;

        if ( (title = Common.recvString(inStream)) == null)
            return false;

        if ( (description = Common.recvString(inStream)) == null)
            return false;

        if ( (nshares = Common.recvInt(inStream)) == -1)
            return false;
        if ( (price = Common.recvInt(inStream)) == -1)
            return false;

        if ( (numMinShares = Common.recvInt(inStream)) == -1)
            return false;

        //Receive Topics
        if ( (topicsArray = receiveData()) == null )
            return false;

        //FIXME: prints
        System.out.println("CÁ ESTÃO OS TÓPICOS: ");
        for (String topico : topicsArray)
                System.out.println("TOPIC: "+topico);

        //Receive Ideas For
        if ( !receiveInt(ideasForArray) )
            return false;

        //Receive Ideas Against
        if ( !receiveInt(ideasAgainstArray))
            return false;

        //Receive Ideas Neutral
        if ( !receiveInt(ideasNeutralArray))
            return false;

        Request createIdeaRequest = null;
        if ( (createIdeaRequest = server.queue.getFirstRequestByUIDAndType(uid,Request.RequestType.CREATE_IDEA)) ==
                null) {
            ArrayList<Object> objects = new ArrayList<Object>(); objects.add(title); objects.add(description); objects.add(this.uid);
            createIdeaRequest = new Request(uid, Request.RequestType.CREATE_IDEA,objects);
            server.queue.enqueue(createIdeaRequest);
        }
        createIdeaRequest.waitUntilDispatched();
        result = (Integer)createIdeaRequest.requestResult.get(0);

        if (result < 0){
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;

            server.queue.dequeue(createIdeaRequest);
            return true;
        }


        Request setSharesIdeaRequest = null;
        if ( (setSharesIdeaRequest = server.queue.getFirstRequestByUIDAndType(uid,Request.RequestType.SET_SHARES_IDEA)) ==
                null) {
            ArrayList<Object> objects = new ArrayList<Object>(); objects.add(this.uid); objects.add(result);
            objects.add(nshares);objects.add(price);objects.add(numMinShares);
            setSharesIdeaRequest = new Request(uid, Request.RequestType.SET_SHARES_IDEA,objects);
            server.queue.enqueue(setSharesIdeaRequest);
        }
        setSharesIdeaRequest.waitUntilDispatched();
        result_shares = (Boolean)setSharesIdeaRequest.requestResult.get(0);

        if (!result_shares){
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) )
                return false;

            server.queue.dequeue(createIdeaRequest);
            server.queue.dequeue(setSharesIdeaRequest);
            return true;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
            return false;

        ////
        //  Take care of the topics
        ////

        //1st - Verify if the topics' names are correct
        ArrayList<Request> requests1 = new ArrayList<Request>();
        for (int i = 0; i < topicsArray.length; i++) {
            topic = topicsArray[i];
            if (topic.length() > limit_characters_topic) {//Topic name too long, tell that to the client
                if (! Common.sendMessage(Common.Message.ERR_TOPIC_NAME, outStream))
                    return false;
                if (! Common.sendString(topic, outStream))//Send name of the topic that was wrong
                    return false;
            }

            //2nd - Actually bind them to the idea


            Request setTopicsIdeaRequest = null;
            if ((setTopicsIdeaRequest = server.queue.getNthRequestByUIDAndType(uid,
                    Request.RequestType.SET_TOPICS_IDEA,i+1)) ==
                    null) {
                ArrayList<Object> objects = new ArrayList<Object>();
                objects.add(result);
                objects.add(topic);
                objects.add(uid);
                setTopicsIdeaRequest = new Request(uid, Request.RequestType.SET_TOPICS_IDEA, objects);
                server.queue.enqueue(setTopicsIdeaRequest);
                requests1.add(setTopicsIdeaRequest);
            }
            setTopicsIdeaRequest.waitUntilDispatched();

            result_topics = (Boolean) setTopicsIdeaRequest.requestResult.get(0);
            if (result_topics) {
                if (! Common.sendMessage(Common.Message.MSG_OK, outStream))
                    return false;
            } else {
                if (! Common.sendMessage(Common.Message.MSG_ERR, outStream)) //ISTO SÓ DA MERDA SE O RMI DER MERDA,
                // FIXME^
                    return false;
            }
        }




        int iState = 0;
        // Take care of the ideas for
        if (!setRelations(result, ideasForArray, 1, requests1, iState))
            return false;

        iState += ideasForArray.size();

        //  Take care of the ideas against
        if (!setRelations(result,ideasAgainstArray,-1, requests1, iState))
            return false;

        iState += ideasAgainstArray.size();

        //  Take care of the ideas neutral
        if(!setRelations(result,ideasNeutralArray,0, requests1, iState))
            return false;

        if ( (reply=Common.recvMessage(inStream)) == Common.Message.MSG_IDEA_HAS_FILE){
            //Receive File
            ObjectInputStream objectStream = null;
            try{
                objectStream = new ObjectInputStream(inStream);
                ficheiro = (NetworkingFile)objectStream.readObject();

                boolean fileResult = false;
                if ( (addFileRequest = server.queue.getFirstRequestByUIDAndType(uid,Request.RequestType.ADD_FILE)) ==
                        null) {
                    ArrayList<Object> objects = new ArrayList<Object>(); objects.add(result); objects.add
                            (ficheiro);
                    addFileRequest = new Request(uid, Request.RequestType.ADD_FILE,objects);
                    server.queue.enqueue(addFileRequest);
                }
                addFileRequest.waitUntilDispatched();
                fileResult = (Boolean)addFileRequest.requestResult.get(0);

                //FIXME: Deal with fileResult

            }catch(IOException i){
                System.out.println("IO Exception"); //FIXME Can't just return (remember to dequeue!!)
                i.printStackTrace();
                return false;
            }catch(ClassNotFoundException c){
                System.out.println("Class not found");//FIXME Can't just return (remember to dequeue!!)
                c.printStackTrace();
                return false;
            }
        }

        //Everything ok
        if(!Common.sendMessage(Common.Message.MSG_OK,outStream))
            return false;


        if ( addFileRequest != null )
            server.queue.dequeue(addFileRequest);
        server.queue.dequeue(createIdeaRequest);
        server.queue.dequeue(setSharesIdeaRequest);
        for (Request r : requests1)
            server.queue.dequeue(r);
        return true;
    }

    ////
    //  Searches ideas from its id and title
    ////
    private boolean handleGet_Idea(){
        int iid;
        String title;
        Idea[] ideas;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;
        if ( (title = Common.recvString(inStream)) == null)
            return false;

        if (iid == -2)
            iid = -1; //To avoid "Because they're trying to hack us" <- To Maxi
        ////
        //FIXME FIXME FIXME MAXI VE ISTO!!!

        try {
            ideas = connection.getRMIInterface().getIdeaByIID(iid,title);
        } catch (RemoteException e) {
            System.err.println("RMI exception while fetching an idea by its IID");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if ( ideas == null) {
            // There is no idea with this ID
            if ( !Common.sendMessage(Common.Message.ERR_NO_SUCH_IID, outStream))
                return false;
        } else {
            // Got the idea, let's send it
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;

            if (!Common.sendInt(ideas.length,outStream))
                return false;

            for (int i=0;i<ideas.length;i++){
                if ( !ideas[i].writeToDataStream(outStream) )
                    return false;
            }

            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        }
        return true;
    }

    ////
    //  Gets an idea from its id
    ////
    private boolean handleGetIdeaByIID() {
        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        int iid;

        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        Idea idea = null;

        try {
            idea = connection.getRMIInterface().getIdeaByIID(iid);
        } catch (RemoteException e) {
            System.err.println("RMI exception while fetching an idea by its IID");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false; //FIXME: Do we really want to return this? WHAT TO DO WHEN RMI IS DEAD?!
        }

        if ( idea == null ) {
            // There is no idea with this ID
            if ( !Common.sendMessage(Common.Message.ERR_NO_SUCH_IID, outStream))
                return false;
        } else {
            // Got the idea, let's send it
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;

            if ( !idea.writeToDataStream(outStream) )
                return false;

            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        }

        return true;
    }

    ////
    //  Sends the list of the topics to the user
    ////
    private boolean handleListTopicsRequest() {
        ServerTopic temp;
        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        ServerTopic[] topics = null;
        try {
            topics = connection.getRMIInterface().getTopics();
            System.out.println("Existem " + topics.length + " topicos");
        } catch (RemoteException e) {
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if ( !Common.sendInt(topics.length,outStream) )
            return false;

        for (int i=0;i<topics.length;i++){
            if (!topics[i].writeToDataStream(outStream))
                return false;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        return true;
    }

    private boolean handleGetSharesNotSell(){
        int iid, shares = -2;
        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Receive Data
        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        try{
            shares = connection.getRMIInterface().getSharesNotSell(iid,uid);
        }catch (RemoteException r){
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        //Confirm data
        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Send data
        if (!Common.sendInt(shares,outStream))
            return false;

        //Send final confirmation
        return Common.sendMessage(Common.Message.MSG_OK, outStream);

    }

    private boolean handleSetSharesNotSell(){
        int iid, numberShares;
        boolean check = false;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Receive data
        if ( (iid = Common.recvInt(inStream)) == -1){
            return false;
        }

        //Receive data
        if ( (numberShares = Common.recvInt(inStream)) == -1){
            return false;
        }

        try{
            check = connection.getRMIInterface().setSharesNotSell(iid, uid, numberShares);
        }catch(RemoteException r){
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        //Send final confirmation
        if (check){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        }

        else{
            if (!Common.sendMessage(Common.Message.MSG_ERR,outStream))
                return false;
        }

        return true;
    }

    private boolean handleGetFile(){
        NetworkingFile ficheiro;
        int iid;
        ObjectOutputStream objectStream = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Get data
        if ((iid = Common.recvInt(inStream)) == -1)
            return false;

        try{
            ficheiro = connection.getRMIInterface().getFile(iid);
        }catch(RemoteException r){
            System.out.println("RemoteException in the handle get File method");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        //Send confirmation
        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Send Data
        try {
            objectStream = new ObjectOutputStream(outStream);
            objectStream.writeObject(ficheiro);
        }catch(IOException e){
            System.err.println("Error while sending file");
            return false;
        }

        //Send Final Confirmation
        return Common.sendMessage(Common.Message.MSG_OK, outStream);

    }

    private boolean handleGetIdeaFile(){
        NetworkingFile file;
        int iid;
        ObjectOutputStream objectstream = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Receive Data
        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        try{
            file = connection.getRMIInterface().getFile(iid);
        }catch(RemoteException r){
            System.err.println("RemoteException in the handle get idea file method!");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (file == null){
            Common.sendMessage(Common.Message.MSG_ERR,outStream);
            return false;
        }
        else{
            //Send Data confirmation
            if (!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;
            try{
                objectstream = new ObjectOutputStream(outStream);
                objectstream.writeObject(file);
            }catch(IOException i){
                System.out.println("Error sending the file");
                return false;
            }

            //Send final confirmation
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;

            return true;
        }
    }

    private boolean handleGetIdeasFiles(){
        int iid = -1;
        Idea[] ideasFiles;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        try{
            ideasFiles = connection.getRMIInterface().getFilesIdeas();
        }catch(RemoteException r){
            System.out.println("RemoteException in the handle get ideas files method");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (ideasFiles == null){
            Common.sendMessage(Common.Message.ERR_NO_FILE,outStream);
            return false;
        }

        //2nd Confirmation Message
        if (!Common.sendMessage(Common.Message.MSG_OK,outStream))
            return false;

        //Send data
        if(!Common.sendInt(ideasFiles.length,outStream))
            return false;

        for (int i=0;i<ideasFiles.length;i++){
            if (!ideasFiles[i].writeToDataStream(outStream))
                return false;
        }

        //Send final confirmation
        return Common.sendMessage(Common.Message.MSG_OK,outStream);
    }

    private boolean handleSetPriceShares(){
        int iid = -1, price = -1;
        boolean check = false;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Receive data
        if ( (iid = Common.recvInt(inStream)) == -1){
            return false;
        }

        if ( (price = Common.recvInt(inStream)) == -1){
            return false;
        }

        try{
           check = connection.getRMIInterface().setPricesShares(iid,uid,price);
        }catch(RemoteException r){
            System.out.println("RemoteException in the handle set prices shares method");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        //Send data confirmation
        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Send final confirmation
        if (check){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        }

        else{
            if (!Common.sendMessage(Common.Message.MSG_ERR,outStream))
                return false;
        }

        return true;
    }

    private boolean handleGetIdeaShares(){
        int iid = -1;
        String[] ideaShares = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        //Receive data
        if ( (iid = Common.recvInt(inStream)) == -1){
            return false;
        }

        try{
            ideaShares = connection.getRMIInterface().getIdeaShares(iid,uid);
        }catch(RemoteException r){
            System.err.println("RemoteException in the handle get idea shares method!!");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (ideaShares == null){
            if (!Common.sendMessage(Common.Message.MSG_ERR,outStream))
                return false;
        }else{
            //Send data confirmation
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        }

        //Send Data
        if (!Common.sendInt(ideaShares.length,outStream))
            return false;

        for (int i=0;i<ideaShares.length;i++){
            if (!Common.sendString(ideaShares[i],outStream))
                return false;
        }

        //Send final confirmation
        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        return true;
    }

    private boolean handleDeleteIdea() {
        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        /*
          We have developed our system assuming that if there is a crash after the Request has been added to the queue,
        then the user will know this when reconnecting (the server checks for pending requests). Since the user knows
         it, it is responsible for skipping over the sending of all the data, and instead jump right to the part where
         it waits for an answer. So we do it here as well: if the user has already marked a pending request, we just
         ignore receiving the Idea data and wait for that request to be dispatched. If not, then it means there was
         no enqueued request and the user should send us the data again.
         */
        Request removeIdeaRequest = null;
        int result = 0;

        int iid;
        Idea idea = null;

        if ( (removeIdeaRequest = server.queue.getFirstRequestByUIDAndType(uid,Request.RequestType.DELETE_IDEA)) ==
                null) {
            // The user should send us the data, sinc there was no pending request
            if ( (iid = Common.recvInt(inStream)) == -1)
                return false;

            try {
                idea = connection.getRMIInterface().getIdeaByIID(iid);
            }catch(RemoteException r){
                System.err.println("Existiu uma remoteException! " + r.getMessage());
                connection.onRMIFailed();
                server.removeSocket(socket);
            }

            // At this point we will only send ONE message:
            // --> ERR_NO_SUCH_IID: In case we've found no idea with this IDD
            // --> ERR_IDEA_HAS_CHILDREN: In case we've found it, but it has children
            // --> MSG_OK: In case everything's fine

            if ( idea == null ) {
                if ( !Common.sendMessage(Common.Message.ERR_NO_SUCH_IID, outStream))
                    return false;
                return true;
            }

            ArrayList<Object> objects = new ArrayList<Object>(); objects.add(idea); objects.add(uid);
            removeIdeaRequest = new Request(uid, Request.RequestType.DELETE_IDEA,objects);
            //FIXME: This is right where we'd set the user's state to NEED_DISPATCH (request made)
            server.queue.enqueue(removeIdeaRequest);
        } else {
            // There is already a request, we only need to receive messages and ignore them
            if ( Common.recvInt(inStream) == -1)
                return false;
        }



        // Wait until it's dispatched
        removeIdeaRequest.waitUntilDispatched();
        //FIXME: This is right where we'd set the user's state to NEED_NOTIFY (request handled)
        result = (Integer)removeIdeaRequest.requestResult.get(0);

        if ( result == 1) {
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        } else if (result == -1){

            if ( !Common.sendMessage(Common.Message.ERR_IDEA_HAS_CHILDREN, outStream))
                return false;
        } else {
            if ( !Common.sendMessage(Common.Message.ERR_NOT_FULL_OWNER, outStream))
                return false;
        }

        //FIXME: Right before we dequeue, this is where we'd set the user's state to OK (clearing notify)
        server.queue.dequeue(removeIdeaRequest);
        return true;

    }

    ////
    // Sends a topic to the client
    ////
    private boolean handlegetTopic(){
        int tid;
        String name;
        ServerTopic topic = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if( (tid = Common.recvInt(inStream)) == -1)
            return false;

        if ( (name = Common.recvString(inStream)) == null)
            return false;

        try{
            topic = connection.getRMIInterface().getTopic(tid,name);
        }catch(RemoteException r){
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (topic == null){
            if(!Common.sendMessage(Common.Message.ERR_TOPIC_NOT_FOUND,outStream))
                return false;
        }
        else{
            //Confirm topic is ok
            if(!Common.sendMessage(Common.Message.TOPIC_OK,outStream))
                return false;

            //Send topic
            topic.writeToDataStream(outStream);

            //Send final ok
            if(!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;
        }

        System.out.println("Going to return true");
        return true;
    }

    ////
    //  Sends the history of a given client to that client
    ////
    private boolean handleGetHistory(){
        String[] history = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        try{
            history = connection.getRMIInterface().getHistory(uid);
        }catch(RemoteException r){
            System.err.println("Existiu uma remoteException! " + r.getMessage());
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if (history == null){
            System.err.println("HI! I am in the handleGetHistory and history is null!!!");
            return false;
        }

        //Now send the history
        if ( !Common.sendInt(history.length,outStream))
            return false;

        for (String aHistory : history) {
            if (!Common.sendString(aHistory, outStream))
                return false;
        }

        return Common.sendMessage(Common.Message.MSG_OK, outStream);
    }

    ////
    //  Set the relationship between two ideas
    ////
    private boolean handleSetIdeaRelationship(){
        int iidFather = -2, iidSoon = -2, type = -2;
        boolean devolve = false;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;
        if ( (iidFather = Common.recvInt(inStream)) == -1)
            return false;

        if ( (iidSoon = Common.recvInt(inStream)) == -1)
            return false;

        if ( (type = Common.recvInt(inStream)) == -1)
            return false;

        //Becuase we can't send -1 fields
        if (type == -2)
            type = -1;

        try{
            devolve = connection.getRMIInterface().setIdeasRelations(iidFather,iidSoon,type);
        }catch(RemoteException r){
            System.out.println("Remote exception in the handle set idea relationship");
            connection.onRMIFailed();
            server.removeSocket(socket);
            return false;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        return devolve;
    }

    ////
    //  Only returns false if we lost connection
    ////
    private boolean handleLogin() {
        String user, pwd;

        if ( isLoggedIn() ) {
            // Can't login without logging out!
            Common.sendMessage(Common.Message.MSG_ERR,outStream);
            return true; // Message was handled (note that we return true because not being logged in is not a
                         // connection error
        }

        if ( (user = Common.recvString(inStream)) == null)
            return false;
        if ( (pwd = Common.recvString(inStream)) == null)
            return false;

        ArrayList<Object> objects = new ArrayList<Object>();
        objects.add(user);
        objects.add(pwd);
        Request loginRequest = new Request(uid,Request.RequestType.LOGIN,objects);
        //FIXME: This is right where we'd set the user's state to NEED_DISPATCH (request made)
        server.queue.enqueue(loginRequest);

        // Wait until it's dispatched
        loginRequest.waitUntilDispatched();

        //FIXME: This is right where we'd set the user's state to NEED_NOTIFY (request handled)
        uid = (Integer)loginRequest.requestResult.get(0);

        if (uid != -1){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
            try {
                connection.getRMIInterface().updateUserTime(uid);
            } catch (RemoteException e) {
                System.err.println("RMI exception here!");
                connection.onRMIFailed();
                server.removeSocket(socket);
            }
        } else {
            if ( !Common.sendMessage(Common.Message.MSG_ERR, outStream) ){
                return false;
            }
        }

        //FIXME: Right before we dequeue, this is where we'd set the user's state to OK (clearing notify)
        server.queue.dequeue(loginRequest);

        if ( uid == -1 )
            return true;

        /* Now that the user's logged in, check his queue! */
        Request pendingRequest = null;
        if ( (pendingRequest = server.queue.getFirstRequestByUID(uid)) != null ) {
            if ( pendingRequest.dispatched ) {
                if ( !Common.sendMessage(Common.Message.MSG_USER_NOT_NOTIFIED_REQUESTS, outStream) )
                    return false;
            } else {
                if ( !Common.sendMessage(Common.Message.MSG_USER_HAS_PENDING_REQUESTS, outStream) )
                    return false;
            }
        } else {
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
        }

        // Message was handled successfully
        return true;
    }
}

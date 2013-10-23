import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    private RMI_Interface RMIInterface = null;
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

        RMIInterface = connection.getRMIInterface();

        return RMIInterface != null;
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
            } catch (IOException e)
            {} //FIXME: Should we not ignore this?
            return ;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream)) {
            System.out.println("Connection to a client dropped while starting!");
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
                    RMIInterface.updateUserTime(uid);
                } catch (RemoteException e) {
                    System.err.println("RMI exception here!"); //FIXME: Do the retry mechanism?
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
        server.queue.enqueueRequest(registerUserRequest);
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
        boolean result = false;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( (nome = Common.recvString(inStream)) == null)
            return false;

        if ( (descricao = Common.recvString(inStream)) == null)
            return false;

        Request createTopicsRequest = null;
        if ( (createTopicsRequest = server.queue.getFirstRequestByUIDAndType(uid,Request.RequestType.CREATE_TOPIC))
                == null) {
            ArrayList<Object> objects = new ArrayList<Object>(); objects.add(nome); objects.add(descricao); objects
                    .add(this.uid);
            createTopicsRequest = new Request(uid, Request.RequestType.CREATE_TOPIC,objects);
            server.queue.enqueueRequest(createTopicsRequest);
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
        int iid = -1;
        ServerTopic[] list_topics;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if ( (iid = Common.recvInt(inStream)) == -1)
            return false;

        try{
            list_topics = RMIInterface.getIdeaTopics(iid);
        }catch(RemoteException r){
            System.err.println("Error while getting ideas from topic");
            //FIXME: Handle this
            return false;
        }

        if (!Common.sendInt(list_topics.length,outStream))
            return false;

        for (int i=0;i<list_topics.length;i++){
            if (!list_topics[i].writeToDataStream(outStream))
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
        int topicid = -1;
        Idea[] ideaslist = null;

        if ( !isLoggedIn() ) {
            return Common.sendMessage(Common.Message.ERR_NOT_LOGGED_IN, outStream);
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        if ( (topicid = Common.recvInt(inStream)) == -1)
            return false;

        //Now we go to the database and get the ideas

        try{
            ideaslist = RMIInterface.getIdeasFromTopic(topicid);

            if (!Common.sendInt(ideaslist.length,outStream))
                return false;

            //Send ideas
            for (int i=0;i<ideaslist.length;i++){
                if(!ideaslist[i].writeToDataStream(outStream))
                    return false;
            }

            if (!Common.sendMessage(Common.Message.MSG_OK,outStream))
                return false;

        }catch (RemoteException r){
            System.err.println("Error while getting ideas from topic");
            //FIXME: Handle this
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
                server.queue.enqueueRequest(setIdeasRelationsRequest);
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
             ideasList = RMIInterface.getIdeaRelations(iid,type);
        }catch(RemoteException r){
            System.err.println("RemoteException in the handleGetIdeasRelation method");
            return false;
        }

        if (!Common.sendInt(ideasList.length,outStream))
            return false;

        for (int i=0;i<ideasList.length;i++){
            if (!ideasList[i].writeToDataStream(outStream))
                return false;
        }

        if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
            return false;

        return true;
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
             userIdeas = RMIInterface.getIdeasFromUser(uid);
        }catch (RemoteException r){
            System.err.println("Error while getting user's ideas");
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
            server.queue.enqueueRequest(createIdeaRequest);
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
            server.queue.enqueueRequest(setSharesIdeaRequest);
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
                server.queue.enqueueRequest(setTopicsIdeaRequest);
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



        ArrayList<Request> requests1 = new ArrayList<Request>();
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
                    server.queue.enqueueRequest(addFileRequest);
                }
                addFileRequest.waitUntilDispatched();
                fileResult = (Boolean)addFileRequest.requestResult.get(0);

                //FIXME: Deal with fileResult

            }catch(IOException i){
                System.out.println("IO Exception");
                i.printStackTrace();
                return false;
            }catch(ClassNotFoundException c){
                System.out.println("Class not found");
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

        try {
            ideas = RMIInterface.getIdeaByIID(iid,title);
        } catch (RemoteException e) {
            System.err.println("RMI exception while fetching an idea by its IID");
            return false; //FIXME: Do we really want to return this? WHAT TO DO WHEN RMI IS DEAD?!
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
            idea = RMIInterface.getIdeaByIID(iid);
        } catch (RemoteException e) {
            System.err.println("RMI exception while fetching an idea by its IID");
            return false; //FIXME: Do we really want to return this? WHAT TO DO WHEN RMI IS DEAD?!
        }

        if ( idea == null) {
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
            topics = RMIInterface.getTopics();
            System.out.println("Existem " + topics.length + " topicos");
        } catch (RemoteException e) {
            //FIXME: Handle this
            e.printStackTrace();
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
            shares = RMIInterface.getSharesNotSell(iid,uid);
        }catch (RemoteException r){
            r.printStackTrace();
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
            check = RMIInterface.setSharesNotSell(iid, uid, numberShares);
        }catch(RemoteException r){
            r.printStackTrace();
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
           check = RMIInterface.setPricesShares(iid,uid,price);
        }catch(RemoteException r){
            System.out.println("RemoteException in the handle set prices shares method");
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
            ideaShares = RMIInterface.getIdeaShares(iid,uid);
        }catch(RemoteException r){
            System.err.println("RemoteException in the handle get idea shares method!!");
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
        boolean result = false;

        int iid;
        Idea idea = null;

        if ( (removeIdeaRequest = server.queue.getFirstRequestByUIDAndType(uid,Request.RequestType.DELETE_IDEA)) ==
                null) {
            // The user should send us the data, sinc there was no pending request
            if ( (iid = Common.recvInt(inStream)) == -1)
                return false;

            try {
                idea = RMIInterface.getIdeaByIID(iid);
            }catch(RemoteException r){
                //FIXME: Handle this
                System.err.println("Existiu uma remoteException! " + r.getMessage());
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

            ArrayList<Object> objects = new ArrayList<Object>(); objects.add(idea);
            removeIdeaRequest = new Request(uid, Request.RequestType.DELETE_IDEA,objects);
            //FIXME: This is right where we'd set the user's state to NEED_DISPATCH (request made)
            server.queue.enqueueRequest(removeIdeaRequest);
        } else {
            // There is already a request, we only need to receive messages and ignore them
            if ( Common.recvInt(inStream) == -1)
                return false;
        }



        // Wait until it's dispatched
        removeIdeaRequest.waitUntilDispatched();
        //FIXME: This is right where we'd set the user's state to NEED_NOTIFY (request handled)
        result = (Boolean)removeIdeaRequest.requestResult.get(0);

        if ( result ) {
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream))
                return false;
        } else {
            // FIXME: Currently we __KNOW__ that if removeIdea fails, this is why it happened. it may change in the
            // future.
            if ( !Common.sendMessage(Common.Message.ERR_IDEA_HAS_CHILDREN, outStream))
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
            topic = RMIInterface.getTopic(tid,name);
        }catch(RemoteException r){
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
            history = RMIInterface.getHistory(uid);
        }catch(RemoteException r){
            //FIXME: Handle this
            //e.printStackTrace();
            System.err.println("Existiu uma remoteException! " + r.getMessage());
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
            devolve = RMIInterface.setIdeasRelations(iidFather,iidSoon,type);
        }catch(RemoteException r){
            System.out.println("Remote exception in the handle set idea relationship");
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
        server.queue.enqueueRequest(loginRequest);

        // Wait until it's dispatched
        loginRequest.waitUntilDispatched();

        //FIXME: This is right where we'd set the user's state to NEED_NOTIFY (request handled)
        uid = (Integer)loginRequest.requestResult.get(0);

        if (uid != -1){
            if ( !Common.sendMessage(Common.Message.MSG_OK, outStream) )
                return false;
            try {
                RMIInterface.updateUserTime(uid);
            } catch (RemoteException e) {
                System.err.println("RMI exception here!"); //FIXME: Do the retry mechanism? ALSO,
                // should this really be here
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

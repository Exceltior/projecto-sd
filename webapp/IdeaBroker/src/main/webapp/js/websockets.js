/***
 * INCLUDE NOTTY AND AJAXCOMMON FIRST!
 ****/
function initializeWebSockets() { // URI = ws://10.16.0.165:8080/chat/chat
    connect('ws://' + window.location.host + '/chat');
}

function connect(host) { // connect to the host websocket servlet
    if ('WebSocket' in window)
        websocket = new WebSocket(host);
    else if ('MozWebSocket' in window)
        websocket = new MozWebSocket(host);
    else {
        return;
    }

    websocket.onopen    = onOpen; // set the event listeners below
    websocket.onclose   = onClose;
    websocket.onmessage = onMessage;
    websocket.onerror   = onError;
}
function onOpen(event) {
    console.log('Connected to ' + window.location.host + '.');
    //makeNotification("Hello World!", "A Mariana é linda!!!!")
}

function onClose(event) {
    console.log("Websocket closed!");
}

function onMessage(message) { // print the received message
    console.log(message);
    console.log(message.data);
    not = $.parseJSON(message.data);

    if ( not.type == "TAKEOVER") {
        makeNotification("Take Over da tua ideia!",
            "As tuas shares da idea "+not.iid+" foram reclamadas pelo root user ao valor de mercado de  "+not.marketValue+ " por um total de " + not.total+" DEICoins!");
    } else if ( not.type == "MARKETVALUE") {
        console.log("New marketvalue!");
        updateMarketValue(not.iid, not.marketValue);
    } else {

        numShares = not.numShares;
        pricePerShareTransaction = not.pricePerShare;
        username=not.username;
        iid=not.iid;
        currentShares=not.currentShares;
        money=not.money;
        currPricePerShare=not.currPricePerShare;
        totalInvolved = numShares*pricePerShareTransaction;
        if ( not.type == "BOUGHT" ) {
            makeNotification("Shares Compradas", "Adquiriste "+numShares+" shares da ideia "+iid
                +" a "+pricePerShareTransaction+" DEICoins cada, para um total de "+totalInvolved
                +" DEICoins ao user "+username);

            console.log("We just bought "+numShares+" at "+pricePerShareTransaction+ " for a total of "+totalInvolved+" from "+username+" for idea "+iid);
        } else {
            makeNotification("Shares Vendidas", "Vendeste "+numShares+" shares da ideia "+iid
                +" a "+pricePerShareTransaction+" DEICoins cada, para um total de "+totalInvolved
                +" DEICoins ao user "+username);
            console.log("We just sold "+numShares+" at "+pricePerShareTransaction+ " for a total of "+totalInvolved+" to "+username+" for idea "+iid);
        }

        if ( haveIdeaOnWebpage(iid) ) {
            setNumSharesForidea(iid, currentShares);
            setSellingPriceIdea(iid, currPricePerShare);
        }
        setUserMoney(money);

        console.log("We currently have "+currentShares+" valuated at "+currPricePerShare+" each. And we have "+money+" DEICoins.");
    }
}

function onError(event) {
    console.log('WebSocket error (' + event.data + ').');
}
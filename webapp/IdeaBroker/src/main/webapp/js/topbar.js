function getUserMoney() {
    return parseFloat($('#currmoney').text());
}

function setUserMoney(money) {
    $('#currmoney').text(money);
}


// Load the SDK Asynchronously
(function (d) {
    var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
    if (d.getElementById(id)) { return; }
    js = d.createElement('script'); js.id = id; js.async = true;
    js.src="//connect.facebook.net/en_US/all.js";
    ref.parentNode.insertBefore(js, ref);
}(document));
var token;
gFacebookAssociateDialog = null;

function doFacebookAssociate(money) {

    var message, header, type;
    message =
        $('<div style="text-align:center;"><div style="font-size:40pt;">' +
            '<span style="color: #428bca" class="glyphicon glyphicon-thumbs-up"></span>' +
            '</div>&nbsp;Verifique a sua identidade e autorize a nossa aplicação a espalhar pelo Facebook as suas' +
            'Ideias!</div><div id="auth-status" style="text-align: center"><div id="auth-loggedout"><div ' +
            'class="fb-login-button" autologoutlink="true"' +
            'scope="email,user_checkins,publish_actions,publish_stream,read_stream">Login ' +
            'with Facebook</div></div><div id="auth-loggedin" style="display: none"></div></div>');
    header =
        '<div class="bootstrap-dialog-title">Associar com o Facebook</div><div class="bootstrap-dialog-close-button" style="display: block;"><button class="close"  onclick="gFacebookAssociateDialog.close();"><span class="glyphicon glyphicon-remove"></span></button></div>';
    type = BootstrapDialog.TYPE_PRIMARY;
    var dialog = new BootstrapDialog({
        size: BootstrapDialog.SIZE_LARGE,
        type: type,
        message: message,
        closable:true
    });
    gFacebookAssociateDialog = dialog;
    dialog.realize();
    dialog.getModalHeader().html(header);
    dialog.getModalFooter().hide();
    dialog.open();

    var check = function(){
        if($('#auth-status').is(":visible")){
            FB.init({
                appId:  436480809808619/*687899411244345*/,
                channelUrl: '//' + window.location.hostname + '/channel', // Path to your   Channel File
                status: true, // check facebookLogin status
                cookie: true, // enable cookies to allow the server to access the session
                xfbml: true  // parse XFBML
            });

            $('#auth-status').show();


            // listen for and handle auth.statusChange events
            FB.Event.subscribe('auth.statusChange', function (response) {

                if (response.authResponse){
                    //alert("Entrei no authResponse");

                    if (response.authResponse.accessToken){
                        //alert("Entrei no accessToken");

                        postJSON("facebookassociate.action",
                            { token:response.authResponse.accessToken }, function(data) {
                                console.log(data);
                            });
                    }
                }
            });
            $("#auth-logoutlink").click(function () { FB.logout(function () { window.location.reload(); }); });
        }
        else {
            setTimeout(check, 1000); // check again in a second
        }
    }

    check();


}
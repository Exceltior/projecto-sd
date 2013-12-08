<!-- STRUTS -->
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!-- END STRUTS -->

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link href="bootstrap-3.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/login.css" rel="stylesheet">
    <link href="css/clouds.css" rel="stylesheet">
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Idea Broker</title>
</head>
<body>

<script>
    // Load the SDK Asynchronously
    (function (d) {
        var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
        if (d.getElementById(id)) { return; }
        js = d.createElement('script'); js.id = id; js.async = true;
        js.src="//connect.facebook.net/en_US/all.js";
        ref.parentNode.insertBefore(js, ref);
    }(document));
    var token;

    function doPost(url,data){
        $.ajax({
            type: "POST",
            url: url,
            data: data,
            dataType: "html",
            success: function(data, textStatus) {
                console.log(data);
                console.log(textStatus);
                document.open();
                document.write(data);
                document.close();
            }
        });
    }

    // Init the SDK upon load
    function deisugaalmgas() {
        //window.fbAsyncInit = function () {
        FB.init({
            appId:  436480809808619/*687899411244345*/,
            channelUrl: '//' + window.location.hostname + '/channel', // Path to your   Channel File
            status: true, // check facebookLogin status
            cookie: true, // enable cookies to allow the server to access the session
            xfbml: true  // parse XFBML
        });

        $('#botaoquesugaalmasfuriosamente').hide();
        $('#auth-status').show();


        // listen for and handle auth.statusChange events
        FB.Event.subscribe('auth.statusChange', function (response) {

            if (response.authResponse){
                //alert("Entrei no authResponse");

                if (response.authResponse.accessToken){
                    //alert("Entrei no accessToken");

                    doPost("http://" + window.location.host + "/registerfacebook.action",
                            { token:response.authResponse.accessToken });
                }
            }
        });
        $("#auth-logoutlink").click(function () { FB.logout(function () { window.location.reload(); }); });
    }
</script>

<div style="position:relative">
    <div id="clouds" style="position:relative; ">
        <div class="cloud x1"></div>
        <div class="cloud x2"></div>
        <div class="cloud x3"></div>
        <div class="cloud x4"></div>
        <div class="cloud x5"></div>
        <div class="cloud x2"></div>
        <div class="cloud x1"></div>

        <div style="margin-top:-350px">
            <div class="container">
                <div class="row">
                    <div class="col-md-4 col-md-offset-4">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title">Registo</h3>
                            </div>
                            <div class="panel-body">
                                <form action="register.action" method="POST" accept-charset="UTF-8" role="form">
                                    <fieldset>
                                        <div class="form-group">
                                            <input class="form-control" placeholder="Nome de Utilizador"
                                                   name="username" type="text">
                                        </div>
                                        <div class="form-group">
                                            <input class="form-control" placeholder="E-mail"
                                                   name="email" type="text">
                                        </div>
                                        <div class="form-group">
                                            <input class="form-control" placeholder="Password" name="password" type="password" value="">
                                        </div>
                                        <input class="btn btn-lg btn-success btn-block" type="submit" value="Registar">
                                    </fieldset>
                                </form>
                                <hr/>
                                <div style="text-align: center;"><h4>OU</h4></center>
                                    <a href="#"
                                       id="botaoquesugaalmasfuriosamente" class="btn btn-lg btn-facebook btn-block"
                                       onclick="deisugaalmgas()">Criar conta
                                        com o Facebook</a>
                                    <div id="auth-status" style="display:none">
                                        <div id="auth-loggedout">
                                            <div class="fb-login-button" autologoutlink="true"
                                                 scope="email,user_checkins,publish_actions,publish_stream,read_stream">Login com o Facebook</div>
                                        </div>

                                        <div id="auth-loggedin" style="display: none"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="js/jquery.js"></script>
<script src="bootstrap-3.0.2/dist/js/bootstrap.min.js"></script>

</body>
</html>

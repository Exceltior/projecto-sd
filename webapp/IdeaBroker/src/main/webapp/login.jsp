<!-- STRUTS -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
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
    <script type="text/javascript" src="http://code.jquery.com/jquery-latest.js"></script>
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
            dataType: "json",
            success: function(data, textStatus) {
                 console.log(data);
                 console.log(textStatus);
                if (data.redirect) {
                    // data.redirect contains the string URL to redirect to
                    alert(data.redirect);
                    window.location.href = data.redirect;
                }
                else {
                    // data.form contains the HTML for the replacement form
                    alert("Deu MERDA");
                }
            }
        });
    }

    // Init the SDK upon load
    window.fbAsyncInit = function () {
        FB.init({
            appId:  436480809808619,
            channelUrl: '//' + window.location.hostname + '/channel', // Path to your   Channel File
            status: true, // check login status
            cookie: true, // enable cookies to allow the server to access the session
            xfbml: true  // parse XFBML
        });

        // listen for and handle auth.statusChange events
        FB.Event.subscribe('auth.statusChange', function (response) {


            if (response.authResponse) {
                // user has auth'd your app and is logged into Facebook
                //$('#AccessToken').val(response.authResponse.accessToken);

                FB.api('/me', function (me) {
                    console.log(me);
                    if (me.id) {
                        token = me.token;


                        doPost("http://" + window.location.host + "/loginfacebook.action",
                                { id:"MERDA" });

                    }
                });
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
                                <h3 class="panel-title">Login via site</h3>
                            </div>
                            <div class="panel-body">
                                <form action="login.action" method="POST" accept-charset="UTF-8" role="form">
                                    <fieldset>
                                        <div class="form-group">
                                            <input class="form-control" placeholder="E-mail" name="username" type="text">
                                        </div>
                                        <div class="form-group">
                                            <input class="form-control" placeholder="Password" name="password" type="password" value="">
                                        </div>
                                        <input class="btn btn-lg btn-success btn-block" type="submit" value="Login">
                                    </fieldset>
                                </form>
                                <hr/>
                                <div style="text-align: center;"><h4>OR</h4></center>
                                <!-- <a href="#" class="btn btn-lg btn-facebook btn-block">Login with Facebook</a> -->
                                    <div id="auth-status">
                                        <div id="auth-loggedout">
                                            <div class="fb-login-button" autologoutlink="true" scope="email,user_checkins">Login  with Facebook</div>
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
<script src="jquery.js"></script>
<script src="bootstrap-3.0.2/dist/js/bootstrap.min.js"></script>

</body>
</html>

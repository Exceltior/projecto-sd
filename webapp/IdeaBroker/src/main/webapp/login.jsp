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
    <title></title>
</head>
<body>
<div style="position:relative">
    <div id="clouds" style="position:relative">
        <div class="cloud x1"></div>
        <!-- Time for multiple clouds to dance around -->
        <div class="cloud x2"></div>
        <div class="cloud x3"></div>
        <div class="cloud x4"></div>
        <div class="cloud x5"></div>

        <div style="margin-top:-150px">
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
                                <input class="btn btn-lg btn-facebook btn-block" type="submit" value="Login via facebook">
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

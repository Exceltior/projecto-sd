<!-- STRUTS -->
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<!-- END STRUTS -->

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link href="bootstrap-3.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/login.css" rel="stylesheet">
    <link href="css/clouds.css" rel="stylesheet">
    <link href="css/simple-sidebar.css" rel="stylesheet">
    <link href="css/banner.css" rel="stylesheet">
    <link href="css/bootstrap-dialog.css" rel="stylesheet">
    <!--<link href="css/3dbtn.css" rel="stylesheet">-->
    <title></title>
    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/noty/jquery.noty.js"></script>
    <script type="text/javascript" src="js/noty/layouts/top.js"></script>
    <script type="text/javascript" src="js/noty/layouts/topRight.js"></script>
    <script type="text/javascript" src="js/noty/themes/default.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/notifications.js"></script>
    <script type="text/javascript" src="js/websockets.js"></script>
    <script type="text/javascript" src="js/topbar.js"></script>
    <script type="text/javascript" src="js/sidebar.js"></script>
    <script type="text/javascript">

        /**
         * WEBSOCKETS
         */
        window.onload = function() { // execute once the page loads
            initializeWebSockets();
        }

    </script>
</head>
<body>
<div id="wrapper">
<!-- Sidebar -->
<div id="sidebar-wrapper">
    <jsp:include page="elements/sidebarcontent.jsp" />
</div>

<!-- Page content -->
<div id="page-content-wrapper">
    <div class="content-header">
        <jsp:include page="elements/topbarcontent.jsp" />
    </div>
    <!-- Keep all page content within the page-content inset div! -->
    <div class="page-content inset" style=" margin: -20px">
        <div style="position:relative">
            <div id="clouds" style="position:relative; margin: -20px; margin-top: -40px; z-index: -20;">
                <div class="cloud x1"></div>
                <div class="cloud x2"></div>
                <div class="cloud x3"></div>
                <div class="cloud x4"></div>
                <div class="cloud x5"></div>
                <div class="cloud x2"></div>
                <div class="cloud x1"></div>
            </div>
            <div style="position:absolute; top:0; left:0; width:100%">
                <div class="row" style="margin:10px; margin-top:30px">

                    <div class="col-md-8 col-md-offset-2">
                        <h2 style="text-align:center">Hall of Fame</h2>
                        <div class="list-group text-center" style = "margin-top:25px;">
                            <s:iterator var="i" step="1" value="ideas">
                                <div class="list-group-item" id="idea<s:property value="id" />"
                                     style = "margin-top:25px; padding-bottom:50px;">
                                        <%-- href="viewidea.action?iid=<s:property value="iid" />" --%>
                                    <h4
                                            class="list-group-item-heading"><s:property value="title" /></h4>
                                    <p class="list-group-item-text">
                                    <div style="height: 45px">
                                        <div style="float:left; margin-top: 5px;" id="buttonsleft<s:property
                                                     value="id" />">
                                            <a id="marketvaluebtn<s:property value="id" />"
                                               class = "btn btn-info btn-sm"
                                               type="button"><span
                                                    class="glyphicon glyphicon-tags"
                                                    ></span> Valor de Mercado:
                                                        <span
                                                                id="marketvalue<s:property value="id" />"><s:property
                                                                value="marketValue" /></span></a>
                                        </div>
                                    </div>
                                        <%--Watchlist: <s:property value="inWatchList" />
                                        Owned: <s:property value="numSharesOwned" />
                                        Percent Owned: <s:property value="percentOwned" />--%>
                                    <div>
                                        <s:property value="body" />
                                        <hr/>
                                    </div>

                                    <!-- Topic hashtags -->
                                    <div style="float:left; min-height:40px;" id="ideatopics<s:property
                                             value="id" />">
                                        <s:iterator var="i" step="1" value="topics">
                                            <a href="listideas.action?mode=topic&tid=<s:property value="id" />"
                                               style="padding-right:5px;">

                                                        <span
                                                                class="label label-primary">
                                                         #<s:property value="title" />
                                                    </span></a>
                                        </s:iterator>
                                    </div>
                                </div>
                            </s:iterator>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
<script src="bootstrap-3.0.2/dist/js/bootstrap.min.js"></script>
<script src="js/bootstrap-dialog.js"></script>
</body>
</html>


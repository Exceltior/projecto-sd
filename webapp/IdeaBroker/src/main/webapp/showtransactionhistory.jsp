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
                            <h2 style="text-align:center">Histórico de Transacções</h2>
                            <table class="table table-hover">
                                <tr class="info" style="background-color:skyblue">
                                    <th>Ideia</th>
                                    <th>Comprador</th>
                                    <th>Vendedor</th>
                                    <th># Shares</th>
                                    <th>Preço</th>
                                    <th>Total</th>
                                    <th>Data</th>
                                </tr>
                            <div class="list-group text-center" >
                                <s:iterator var="i" step="1" value="historyList">
                                    <tr
                                            <s:if test="top.bought">class="success"</s:if>
                                            <s:else>class="danger"</s:else>
                                            >
                                        <td><s:property value="title"/></td>
                                        <td <s:if test="top.bought">style="font-weight: bold"</s:if>>
                                            <s:property value="buyer"/></td>
                                        <td <s:if test="!top.bought">style="font-weight: bold"</s:if>>
                                            <s:property value="seller"/></td>
                                        <td><s:property value="numShares"/></td>
                                        <td><s:property value="pricePerShare"/></td>
                                        <td><b></b><s:property value="total"/></b></td>
                                        <td><i></i><s:property value="Date"/></i></td>
                                    </tr>
                                </s:iterator>
                            </div>
                                </table>
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

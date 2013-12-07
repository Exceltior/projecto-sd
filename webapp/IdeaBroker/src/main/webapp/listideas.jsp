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
    <script type="text/javascript" src="js/viewideas.js"></script>
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
    <ul class="sidebar-nav">
        <li class="sidebar-brand"><a href="#">Team TransformadaZ</a></li>
        <li style="margin-top:100px;"><a href="listtopics.action"><span
                class="glyphicon glyphicon-list">&nbsp;Ver
                Tópicos</span><span>&nbsp;</span></a></li>
        <li><a
                href="listideas.action?mode=watchlist"><span class="glyphicon glyphicon-eye-open">&nbsp;
                Watchlist</span><span>&nbsp;</span></a></li>
        <li><a href="listideas.action?mode=userideas"><span class="glyphicon glyphicon-cloud">&nbsp;As Minhas
                Ideias</span><span>&nbsp;</span></a></li>
        <li><a href="viewhalloffame.action"><span class="glyphicon glyphicon-fire">&nbsp;Hall
                of Fame</span><span>&nbsp;</span></a></li>
        <li><a href="#" onclick="createIdea();"><span class="glyphicon glyphicon-edit">&nbsp;Adicionar
                Ideia</span><span>&nbsp;</span></a></li>
        <li><a href="#" onclick="searchIdea()"><span class="glyphicon glyphicon-search"  style="z-index:0"></span><span
                class="glyphicon glyphicon-cloud" style="margin-left:-5px; z-index:1">&nbsp;Pesquisar
                Ideias</span><span>&nbsp;</span></a> </li>
    </ul>
</div>

<!-- Page content -->
<div id="page-content-wrapper">
    <div class="content-header">
        <div class="bs-header">
            <h1  style="margin-left: -10px;">Idea Broker</h1>
            <p> As suas ideias. O nosso mercado. </p>
        </div>
        <nav class="navbar navbar-default navbar-static-top" role="navigation">
            <ul class="nav nav-pills nav-justified"  style="font-size: 18pt;">
                <li><a href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;
                    <s:property value="#session.client.username"/></a></li>
                <li><a href="#" id="coins"><span class="glyphicon glyphicon-euro"></span>&nbsp;<span
                        id="currmoney"><s:property
                        value="#session.client.coins"/></span> DEICoins</a></li>
                <li><a href="#" id="numNotifications"><span class="glyphicon
                     glyphicon-envelope"></span>&nbsp;<s:property value="#session.client.numNotifications"/> Novas
                    Mensagens</a></li>
            </ul>
        </nav>
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
                        <h2 style="text-align:center"><s:property value="title" /></h2>
                        <div class="list-group text-center" style = "margin-top:25px;">
                            <s:iterator var="i" step="1" value="ideas">
                                <div class="list-group-item" id="idea<s:property value="id" />"
                                     style = "margin-top:25px; padding-bottom:50px;">
                                        <%-- href="viewidea.action?iid=<s:property value="iid" />" --%>
                                    <h4
                                            class="list-group-item-heading"><s:property value="title" /></h4>
                                    <p class="list-group-item-text">
                                    <div style="height: 25px">
                                        <div style="float:right; white-space:nowrap;" id="ideatags<s:property
                                             value="id" />">
                                            <!-- Labels here -->
                                                    <span
                                                            <s:if test="top.percentOwned == 0.0">style="display:none"</s:if>
                                                            class="label label-info" id="ownidea<s:property value="id" />"><span
                                                            class="glyphicon glyphicon-ok"></span><span
                                                            id="numshares<s:property value="id" />"><s:property
                                                            value="numSharesOwned" /></span> shares (
                                                        <span
                                                                id="percentshares<s:property value="id" />">
                                                            <s:property
                                                                    value="percentOwned" /></span>%)
                                                    </span>
                                            <!-- Watchlist Label -->
                                                    <span
                                                            <s:if test="!top.inWatchList">style="display:none"</s:if>
                                                            class="label label-success"
                                                            id="watchlistlabel<s:property value="id"/>"><span class="glyphicon glyphicon-eye-open"></span>Na Watchlist
                                                        <a href="#" type="button"
                                                           onclick="removeFromWatchlist(<s:property
                                                                   value="id" />);"><span
                                                                class="glyphicon glyphicon-remove"></span></a> </span>

                                        </div>
                                        <div style="float:left; white-space:nowrap;" id="idealeftarea<s:property
                                             value="id" />">
                                            <!-- Left buttons -->
                                            <s:if test="!#session.client.adminStatus">
                                                <!-- Set share price -->

                                                <div class="input-append"
                                                     id="setsharepriceeditbox<s:property value="id" />"
                                                     <s:if test="top.percentOwned == 0.0">style="display:none"</s:if>>

                                                        <span
                                                                class="glyphicon glyphicon-euro"></span>
                                                    Vender a
                                                    <input name="text" id="sellingprice<s:property value="id" />"
                                                           value="<s:property value="sellingPrice" />"
                                                           style="width:50px;"
                                                           onkeyup="sharePriceChanged(<s:property
                                                                   value="id" />);"/>
                                                    DEICoins/share <button
                                                        id="btnsellingprice<s:property value="id" />"
                                                        class="btn btn-success btn-sm"
                                                        ><span
                                                        class="glyphicon glyphicon-ok-sign"></span></button>
                                                </div>
                                            </s:if>
                                        </div>
                                    </div>
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
                                        <div style="float:right" id="buttons<s:property
                                                     value="id" />">
                                            <!-- Buttons here -->
                                            <s:if test="#session.client.adminStatus">
                                                <a id="takeover<s:property value="id" />"
                                                   href="#" type="button"
                                                   class="btn btn-success btn-sm"
                                                   onclick="takeover(<s:property value="id" />)">
                                                    <span class="glyphicon glyphicon-fire"></span> Takeover
                                                </a>
                                            </s:if><s:else>
                                            <!-- Delete idea -->
                                            <a id="removeidea<s:property value="id" />"
                                               <s:if test="top.percentOwned != 100.0">style="display: none"</s:if>
                                               href="#" type="button"
                                               class="btn btn-danger btn-sm"
                                               onclick="removeIdea(<s:property value="id" />)">
                                                <span class="glyphicon glyphicon-remove"></span> Apagar Ideia
                                            </a>

                                            <!-- Buy shares-->

                                            <!--data-toggle="modal" href="#myModal"-->
                                            <a <s:if test="top.percentOwned == 100.0">style="display:none"</s:if>
                                               type="button"
                                               class="btn btn-primary btn-sm"
                                               id="buyshares<s:property value="id" />"
                                               onclick="buyShares(<s:property value="id" />);">
                                                <span class="glyphicon glyphicon-cloud"></span><span
                                                    style="margin:-9px; color: #dbd02b"
                                                    class="glyphicon glyphicon-euro"></span>
                                                &nbsp; &nbsp;Comprar Shares
                                            </a>


                                            <a
                                                    <s:if test="top.inWatchList">style="display:none"</s:if>
                                                    href="#" type="button"
                                                    class="btn btn-success btn-sm"
                                                    id="addtowatchlistbtn<s:property value="id" />"
                                                    onclick="addToWatchlist(<s:property value="id" />);">
                                                <span class="glyphicon glyphicon-eye-open"></span> Adicionar
                                                à Watchlist
                                            </a>
                                        </s:else>
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
<script src="bootstrap-dialog.js"></script>
</body>
</html>


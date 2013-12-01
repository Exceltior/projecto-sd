<!-- STRUTS -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
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
    <!--<link href="css/3dbtn.css" rel="stylesheet">-->
    <title></title>
    <script type="text/javascript">
        function addToWatchlist(id){
            btn = "addtowatchlistbtn"+id;
            tags = "ideatags"+id;
            var formData = {iid:id}; //Array
            $.getJSON('addtowatchlist.action', formData,function(data) {

                if ( data.success ) {
                    $('[name='+btn+']').hide();
                    $('[name='+tags+']').html($('[name='+tags+']').html()+'<span class="label label-success" name="watchlistlabel'+id+'"><span class="glyphicon glyphicon-eye-open"></span>Na Watchlist <a href="#" type="button" onclick="removeFromWatchlist('+id+');"><span class="glyphicon glyphicon-remove"></span></a> </span>');
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }

        function removeFromWatchlist(id) {
            btns = "buttons"+id;
            btn = "addtowatchlistbtn"+id;
            tags = "ideatags"+id;
            label = "watchlistlabel"+id;
            var formData = {iid:id}; //Array
            $.getJSON('removefromwatchlist.action', formData,function(data) {

                if ( data.success ) {
                    $('[name='+label+']').hide();
                    $('[name='+btns+']').html($('[name='+btns+']').html()+'<a href="#" type="button" class="btn btn-success btn-sm" name="'+btn+'" onclick="addToWatchlist('+id+');"> <span class="glyphicon glyphicon-eye-open"></span> Adicionar à Watchlist </a>');
                } else {
                    alert("Server Internal Error...RMI is probably down!");
                }
                return true;
            });
        }
    </script>

</head>
<body>

<div id="wrapper">

    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav">
            <li class="sidebar-brand"><a href="#">Team TransformadaZ</a></li>
            <li style="margin-top:100px;"><a
                    href="#"><span class="glyphicon glyphicon-home">&nbsp;Home</span><span style="color:black">
                _</span></a></li>
            <li style="background-color: black"><a href="listtopics.action"><span
                    class="glyphicon glyphicon-list">&nbsp;Ver
                Tópicos</span><span style="color:black">_</span></a></li>
            <li><a
                    href="#"><span class="glyphicon glyphicon-eye-open">&nbsp;Watchlist</span><span
                    style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-cloud">&nbsp;As Minhas Ideias</span><span style="color:black">_</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-bell">&nbsp;Mensagens</span><span style="color:black">_</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-fire">&nbsp;Hall of Fame</span><span style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-search">&nbsp;Pesquisar Tópicos</span><span
                    style="color:black">
                _</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-search"  style="z-index:0"></span><span
                    class="glyphicon glyphicon-cloud" style="margin-left:-5px; z-index:1">&nbsp;Pesquisar
                Ideias</span><span
                    style="color:black">
                _</span></a> </li>

            <s:if
                    test="client.adminStatus == true">
                <li><a href="#"><span class="glyphicon glyphicon-wrench">&nbsp;Painel de Administrador</span><span
                        style="color:black">
                _</span></a></li>
            </s:if>
        </ul>
    </div>

    <!-- Page content -->
    <div id="page-content-wrapper">
        <div class="content-header">
            <div class="bs-header">
                <h1  style="margin-left: -10px;">Idea Broker</h1>
                <p> Your ideas. Our market. </p>
            </div>
            <nav class="navbar navbar-default navbar-static-top" role="navigation">
                <ul class="nav nav-pills nav-justified"  style="font-size: 18pt;">
                    <li><a href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;
                        <s:property value="%{#session.client.username}"/></a></li>
                    <li><a href="#" name="coins"><span class="glyphicon glyphicon-euro"></span>&nbsp;<s:property
                            value="%{#session.client.coins}"/> DEICoins</a></li>
                    <li><a href="#" name="notifications"><span class="glyphicon
                     glyphicon-envelope"></span>&nbsp; Novas
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
                            <h2 style="text-align:center">#<s:property value="topicName" /></h2>
                            <div class="list-group text-center" >
                                <s:iterator var="i" step="1" value="ideas">
                                    <div class="list-group-item">
                                        <%-- href="viewidea.action?iid=<s:property value="iid" />" --%>
                                        <h4
                                                class="list-group-item-heading"><s:property value="title" /><div
                                                "></span></h4>
                                        <p class="list-group-item-text">
                                            <div style="height: 25px">
                                            <div style="float:right" name="ideatags<s:property value="id" />">
                                                <!-- Labels here -->

                                                <!-- Watchlist Label -->
                                                <s:if test="top.inWatchList">

                                                    <span class="label label-success" name="watchlistlabel<s:property
                                                     value="id" />"><span class="glyphicon glyphicon-eye-open"></span>Na Watchlist
                                                        <a href="#" type="button"
                                                           onclick="removeFromWatchlist(<s:property
                                                                value="id" />);"><span
                                                                class="glyphicon glyphicon-remove"></span></a> </span>
                                                </s:if>

                                            </div>
                                        </div>
                                            <div style="height: 45px">
                                                <div style="float:right" name="buttons<s:property
                                                     value="id" />">
                                                    <!-- Buttons here -->
                                                    <s:if
                                                            test="!top.inWatchList">
                                                        <a href="#" type="button"
                                                           class="btn btn-success btn-sm" name="addtowatchlistbtn<s:property value="id" />"
                                                           onclick="addToWatchlist(<s:property value="id" />);">
                                                            <span class="glyphicon glyphicon-eye-open"></span> Adicionar
                                                            à Watchlist
                                                        </a>
                                                    </s:if>
                                                    <!--was btn3d-->
                                                    <a href="deleteidea.action?iid=<s:property value="id" />" type="button"
                                                       class="btn btn-danger btn-sm">
                                                        <span class="glyphicon glyphicon-remove"></span> Apagar Ideia
                                                    </a>
                                                </div>
                                        </div>
                                            Watchlist: <s:property value="inWatchList" />
                                            Owned: <s:property value="numSharesOwned" />
                                            Percent Owned: <s:property value="pecentOwned" />
                                                <s:property value="body" /> </p>
                                    </div>
                                </s:iterator>
                            </div><%--
                            <s:iterator var="i" step="1" value="ideas">
                                <s:url action="viewidea" var="urlTag">
                                    <s:param name="iid" value="top.id" />
                                </s:url>
                                <s:a href="%{urlTag}">Ideia <s:property value="iid" /></s:a><br />

                                <s:property value="id" /><br />
                                <s:property value="title" /><br />
                            </s:iterator>--%>

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

